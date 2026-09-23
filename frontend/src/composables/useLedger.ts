import { computed, ref } from 'vue'
import { createBill, deleteBill, fetchAccounts, fetchBills, fetchCategories, fetchDashboardSummary, saveBudget as saveBudgetRequest, updateBill } from '../api/ledger'
import type { Account, Category, CategoryStat, LedgerRecord, RecordGroup, RecordType, StatisticsPeriod } from '../types/ledger'
import { formatFullDate } from '../utils/format'

const today = new Date()
const selectedMonth = ref(`${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`)
const records = ref<LedgerRecord[]>([])
const statisticsRecords = ref<LedgerRecord[]>([])
const categories = ref<Category[]>([])
const accounts = ref<Account[]>([])
const dashboardExpense = ref(0)
const dashboardIncome = ref(0)
const isLoading = ref(false)
const loadError = ref('')
const monthlyBudgets = ref<Record<string, number>>({})
const statPeriod = ref<StatisticsPeriod>('month')
const statWeekStart = ref('2026-09-20')
const statYear = ref(today.getFullYear())
const categoryKind = ref<RecordType>('expense')
const expandedCategory = ref<string | null>(null)

const expenseCategories = computed(() => categories.value.filter((category) => category.type === 'expense'))
const incomeCategories = computed(() => categories.value.filter((category) => category.type === 'income'))
const defaultAccount = computed(() => accounts.value.find((account) => account.isDefault) ?? accounts.value[0])
const selectedMonthLabel = computed(() => {
  const [year, month] = selectedMonth.value.split('-')
  return `${year} 年 ${Number(month)} 月`
})
const selectedRecords = computed(() => records.value
  .filter((record) => record.date.startsWith(selectedMonth.value))
  .sort((a, b) => b.date.localeCompare(a.date) || b.id - a.id))
const recordGroups = computed<RecordGroup[]>(() => {
  const groups = new Map<string, LedgerRecord[]>()
  selectedRecords.value.forEach((record) => groups.set(record.date, [...(groups.get(record.date) ?? []), record]))
  return [...groups.entries()].map(([date, items]) => ({
    date,
    items,
    expense: items.filter((item) => item.type === 'expense').reduce((sum, item) => sum + item.amount, 0),
  }))
})
const monthExpense = computed(() => dashboardExpense.value)
const monthIncome = computed(() => dashboardIncome.value)
const monthlyBudget = computed(() => monthlyBudgets.value[selectedMonth.value] ?? 0)
const leftBudget = computed(() => monthlyBudget.value - monthExpense.value)
const budgetUsagePercent = computed(() => monthlyBudget.value === 0 ? 0 : Math.min(100, Math.round(monthExpense.value / monthlyBudget.value * 100)))

const statRecords = computed(() => statisticsRecords.value)
const statExpense = computed(() => statRecords.value.filter((item) => item.type === 'expense').reduce((sum, item) => sum + item.amount, 0))
const statIncome = computed(() => statRecords.value.filter((item) => item.type === 'income').reduce((sum, item) => sum + item.amount, 0))
const statAverageLabel = computed(() => statPeriod.value === 'year' ? '月均支出' : '日均支出')
const statAverage = computed(() => {
  if (statPeriod.value === 'year') return statExpense.value / 12
  if (statPeriod.value === 'week') return statExpense.value / 7
  if (statPeriod.value === 'month') return statExpense.value / daysInMonth(selectedMonth.value)
  if (!statRecords.value.length) return 0
  const dates = statRecords.value.map((item) => item.date).sort()
  const first = new Date(`${dates[0]}T00:00:00`)
  const last = new Date(`${dates[dates.length - 1]}T00:00:00`)
  return statExpense.value / Math.max(1, Math.round((last.getTime() - first.getTime()) / 86400000) + 1)
})
const statRangeLabel = computed(() => {
  if (statPeriod.value === 'week') return `${formatFullDate(statWeekStart.value)} ～ ${formatFullDate(addDays(statWeekStart.value, 6))}`
  if (statPeriod.value === 'year') return `${statYear.value} 年`
  if (statPeriod.value === 'all') return '全部账单'
  return selectedMonthLabel.value
})
const categoryTotal = computed(() => categoryKind.value === 'expense' ? statExpense.value : statIncome.value)
const categoryStats = computed<CategoryStat[]>(() => {
  const total = categoryTotal.value || 1
  const result = new Map<string, CategoryStat>()
  statRecords.value.filter((item) => item.type === categoryKind.value).forEach((item) => {
    const current = result.get(item.category)
    if (current) {
      current.amount += item.amount
      current.count += 1
      return
    }
    const category = categories.value.find((value) => value.id === item.categoryId)
    result.set(item.category, {
      id: item.categoryId,
      name: item.category,
      type: item.type,
      icon: item.icon,
      color: item.color,
      sortOrder: category?.sortOrder,
      amount: item.amount,
      count: 1,
      percent: 0,
    })
  })
  return [...result.values()]
    .map((item) => ({ ...item, percent: Math.round(item.amount / total * 100) }))
    .sort((a, b) => b.amount - a.amount)
})
const donutStyle = computed(() => {
  if (!categoryStats.value.length) return 'conic-gradient(#e5e5df 0 100%)'
  let start = 0
  const colors = categoryStats.value.map((item) => {
    const end = start + item.percent
    const slice = `${item.color} ${start}% ${end}%`
    start = end
    return slice
  })
  return `conic-gradient(${colors.join(',')})`
})
const trendData = computed(() => buildTrendData())

async function fetchAllBills(query: Record<string, string | number | undefined>) {
  const items: LedgerRecord[] = []
  let page = 1
  let total = 0
  do {
    const result = await fetchBills({ ...query, page, pageSize: 100 })
    items.push(...result.items)
    total = result.total
    page += 1
  } while (items.length < total)
  return items
}

async function initializeLedger() {
  isLoading.value = true
  loadError.value = ''
  try {
    const [expense, income, accountList] = await Promise.all([
      fetchCategories('expense'),
      fetchCategories('income'),
      fetchAccounts(),
    ])
    categories.value = [...expense, ...income]
    accounts.value = accountList
    await refreshMonthData()
  } catch (error) {
    setLoadError(error)
  } finally {
    isLoading.value = false
  }
}

async function refreshMonthData() {
  const [bills, summary] = await Promise.all([
    fetchAllBills(monthQuery(selectedMonth.value)),
    fetchDashboardSummary(selectedMonth.value),
  ])
  records.value = bills
  dashboardExpense.value = summary.expense
  dashboardIncome.value = summary.income
  monthlyBudgets.value[selectedMonth.value] = summary.budget
  if (statPeriod.value === 'month') statisticsRecords.value = bills
}

async function refreshStatistics() {
  try {
    if (statPeriod.value === 'month') {
      statisticsRecords.value = records.value
      return
    }
    if (statPeriod.value === 'week') {
      statisticsRecords.value = await fetchAllBills({ startDate: statWeekStart.value, endDate: addDays(statWeekStart.value, 6) })
      return
    }
    if (statPeriod.value === 'year') {
      statisticsRecords.value = await fetchAllBills({ startDate: `${statYear.value}-01-01`, endDate: `${statYear.value}-12-31` })
      return
    }
    statisticsRecords.value = await fetchAllBills({})
  } catch (error) {
    setLoadError(error)
  }
}

function selectMonth(month: string) {
  selectedMonth.value = month
  void refreshMonthData().catch(setLoadError)
}

function moveMonth(delta: number) {
  const [year, month] = selectedMonth.value.split('-').map(Number)
  const next = new Date(year, month - 1 + delta, 1)
  selectMonth(`${next.getFullYear()}-${String(next.getMonth() + 1).padStart(2, '0')}`)
}

async function setBudget(value: number) {
  const budget = await saveBudgetRequest(selectedMonth.value, value)
  monthlyBudgets.value[budget.month] = budget.amount
}

function selectStatPeriod(period: StatisticsPeriod) {
  statPeriod.value = period
  expandedCategory.value = null
  void refreshStatistics()
}

function moveStatPeriod(delta: number) {
  if (statPeriod.value === 'week') statWeekStart.value = addDays(statWeekStart.value, delta * 7)
  else if (statPeriod.value === 'month') {
    moveMonth(delta)
    expandedCategory.value = null
    return
  } else if (statPeriod.value === 'year') statYear.value += delta
  expandedCategory.value = null
  void refreshStatistics()
}

function toggleCategory(name: string) {
  expandedCategory.value = expandedCategory.value === name ? null : name
}

function categoryRecords(name: string) {
  return statRecords.value
    .filter((item) => item.type === categoryKind.value && item.category === name)
    .sort((a, b) => b.date.localeCompare(a.date) || b.id - a.id)
}

async function addRecord(type: RecordType, categoryId: number, amount: number, note: string, date: string) {
  const account = defaultAccount.value
  if (!account) throw new Error('请先创建账户')
  const created = await createBill({
    type,
    categoryId,
    accountId: account.id,
    amount,
    date,
    note: note.trim() || undefined,
  })
  await refreshMonthData()
  if (statPeriod.value !== 'month') await refreshStatistics()
  return created
}

async function updateRecord(id: number, type: RecordType, categoryId: number, accountId: number, amount: number, note: string, date: string) {
  const updated = await updateBill(id, { type, categoryId, accountId, amount, date, note: note.trim() || undefined })
  await refreshMonthData()
  if (statPeriod.value !== 'month') await refreshStatistics()
  return updated
}

async function removeRecord(id: number) {
  await deleteBill(id)
  await refreshMonthData()
  if (statPeriod.value !== 'month') await refreshStatistics()
}

function monthQuery(month: string) {
  return { startDate: `${month}-01`, endDate: `${month}-${String(daysInMonth(month)).padStart(2, '0')}` }
}

function daysInMonth(month: string) {
  const [year, value] = month.split('-').map(Number)
  return new Date(year, value, 0).getDate()
}

function addDays(date: string, days: number) {
  const next = new Date(`${date}T00:00:00`)
  next.setDate(next.getDate() + days)
  return next.toISOString().slice(0, 10)
}

function buildTrendData() {
  if (statPeriod.value === 'week') {
    return Array.from({ length: 7 }, (_, index) => {
      const date = addDays(statWeekStart.value, index)
      return { label: ['日', '一', '二', '三', '四', '五', '六'][new Date(`${date}T00:00:00`).getDay()], value: expenseFor(statRecords.value.filter((record) => record.date === date)) }
    })
  }
  if (statPeriod.value === 'year') {
    return Array.from({ length: 12 }, (_, index) => {
      const month = `${statYear.value}-${String(index + 1).padStart(2, '0')}`
      return { label: `${index + 1}月`, value: expenseFor(statRecords.value.filter((record) => record.date.startsWith(month))) }
    })
  }
  if (statPeriod.value === 'all') {
    const months = new Map<string, LedgerRecord[]>()
    statRecords.value.forEach((record) => {
      const month = record.date.slice(0, 7)
      months.set(month, [...(months.get(month) ?? []), record])
    })
    return [...months.entries()]
      .sort(([left], [right]) => left.localeCompare(right))
      .slice(-6)
      .map(([month, items]) => ({ label: `${Number(month.slice(5))}月`, value: expenseFor(items) }))
  }
  return Array.from({ length: daysInMonth(selectedMonth.value) }, (_, index) => {
    const day = index + 1
    const date = `${selectedMonth.value}-${String(day).padStart(2, '0')}`
    return { label: `${day}日`, value: expenseFor(statRecords.value.filter((record) => record.date === date)) }
  })
}

function expenseFor(items: LedgerRecord[]) {
  return items.filter((item) => item.type === 'expense').reduce((sum, item) => sum + item.amount, 0)
}

function setLoadError(error: unknown) {
  loadError.value = error instanceof Error ? error.message : '加载账本失败'
}

export function useLedger() {
  return {
    records, selectedMonth, selectedMonthLabel, selectedRecords, recordGroups,
    monthExpense, monthIncome, monthlyBudget, leftBudget, budgetUsagePercent, statPeriod, statRecords,
    statExpense, statIncome, statAverageLabel, statAverage, statRangeLabel,
    categoryKind, expandedCategory, categoryTotal, categoryStats, donutStyle, trendData,
    expenseCategories, incomeCategories, isLoading, loadError, initializeLedger,
    selectMonth, moveMonth, setBudget, selectStatPeriod, moveStatPeriod,
    toggleCategory, categoryRecords, addRecord, updateRecord, removeRecord,
  }
}

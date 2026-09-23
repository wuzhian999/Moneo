export type RecordType = 'expense' | 'income'
export type StatisticsPeriod = 'week' | 'month' | 'year' | 'all'

export interface Category {
  id: number
  name: string
  type: RecordType
  icon: string
  color: string
  sortOrder?: number
}

export interface Account {
  id: number
  name: string
  icon: string
  color: string
  initialBalance: number
  isDefault: boolean
}

export interface LedgerRecord extends Pick<Category, 'icon' | 'color'> {
  id: number
  type: RecordType
  categoryId: number
  accountId: number
  date: string
  note: string
  amount: number
  category: string
}

export interface RecordGroup {
  date: string
  items: LedgerRecord[]
  expense: number
}

export interface CategoryStat extends Category {
  amount: number
  count: number
  percent: number
}

export interface ApiBill extends LedgerRecord {}

export interface BillPage {
  items: ApiBill[]
  page: number
  pageSize: number
  total: number
}

export interface BillSavePayload {
  type: RecordType
  categoryId: number
  accountId: number
  amount: number
  date: string
  note?: string
}

export interface DashboardSummary {
  month: string
  income: number
  expense: number
  balance: number
  budget: number
}

export interface Budget {
  month: string
  amount: number
}

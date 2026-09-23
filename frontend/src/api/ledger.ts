import { request } from './client'
import type { Account, ApiBill, BillPage, BillSavePayload, Budget, Category, DashboardSummary, RecordType } from '../types/ledger'

export function fetchCategories(type?: RecordType) {
  return request<Category[]>('/categories', {}, type ? { type } : undefined)
}

export function fetchAccounts() {
  return request<Account[]>('/accounts')
}

export function fetchBills(query: Record<string, string | number | undefined>) {
  return request<BillPage>('/bills', {}, query)
}

export function createBill(payload: BillSavePayload) {
  return request<ApiBill>('/bills', { method: 'POST', body: JSON.stringify(payload) })
}

export function updateBill(id: number, payload: BillSavePayload) {
  return request<ApiBill>(`/bills/${id}`, { method: 'PUT', body: JSON.stringify(payload) })
}

export function deleteBill(id: number) {
  return request<void>(`/bills/${id}`, { method: 'DELETE' })
}

export function fetchDashboardSummary(month: string) {
  return request<DashboardSummary>('/dashboard/summary', {}, { month })
}

export function fetchBudget(month: string) {
  return request<Budget>(`/budgets/${month}`)
}

export function saveBudget(month: string, amount: number) {
  return request<Budget>(`/budgets/${month}`, { method: 'PUT', body: JSON.stringify({ amount }) })
}

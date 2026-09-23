export interface ApiResult<T> {
  code: number
  message: string
  data: T
}

export class ApiError extends Error {
  readonly code?: number

  constructor(message: string, code?: number) {
    super(message)
    this.code = code
  }
}

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? '/api'

function buildUrl(path: string, query?: Record<string, string | number | undefined>) {
  const url = new URL(`${apiBaseUrl}${path}`, window.location.origin)
  Object.entries(query ?? {}).forEach(([key, value]) => {
    if (value !== undefined && value !== '') url.searchParams.set(key, String(value))
  })
  return url.toString()
}

export async function request<T>(
  path: string,
  options: RequestInit = {},
  query?: Record<string, string | number | undefined>,
): Promise<T> {
  let response: Response
  try {
    response = await fetch(buildUrl(path, query), {
      ...options,
      headers: {
        Accept: 'application/json',
        ...(options.body ? { 'Content-Type': 'application/json' } : {}),
        ...options.headers,
      },
    })
  } catch {
    throw new ApiError('无法连接后端服务，请确认后端已启动')
  }

  const result = await response.json() as ApiResult<T>
  if (!response.ok || result.code !== 0) {
    throw new ApiError(result.message || '请求失败', result.code || response.status)
  }
  return result.data
}

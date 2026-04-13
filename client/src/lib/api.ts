import createFetchClient from 'openapi-fetch'
import createReactQueryClient from 'openapi-react-query'

import type { paths as CodelistPaths } from '@/api/codelist/schema'
import type { paths as StudentPaths } from '@/api/student/schema'
import type { paths as UserPaths } from '@/api/user/schema'

import { authMiddleware } from './auth'

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8100/api/v1'

// Student Service API client
const studentFetch = createFetchClient<StudentPaths>({ baseUrl: `${BASE_URL}` })
studentFetch.use(authMiddleware)
export const $student = createReactQueryClient(studentFetch)

// User Service API client
const userFetch = createFetchClient<UserPaths>({ baseUrl: `${BASE_URL}` })
userFetch.use(authMiddleware)
export const $user = createReactQueryClient(userFetch)

// Codelist Service API client
const codelistFetch = createFetchClient<CodelistPaths>({ baseUrl: `${BASE_URL}` })
codelistFetch.use(authMiddleware)
export const $codelist = createReactQueryClient(codelistFetch)

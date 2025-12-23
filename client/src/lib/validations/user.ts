import { z } from 'zod'

export const contactSchema = z.object({
  email: z.email().or(z.literal('')),
  phone: z.string().max(20).or(z.literal('')),
  mobile: z.string().max(30).or(z.literal('')),
  dataBox: z
    .string()
    .length(7)
    .regex(/^[a-km-np-z2-9]{7}$/)
    .or(z.literal('')),
})

export const bankAccountSchema = z.object({
  accountNumberPrefix: z
    .string()
    .max(6)
    .regex(/^\d{1,6}$/)
    .or(z.literal('')),
  accountNumberSuffix: z
    .string()
    .max(10)
    .regex(/^\d{1,10}$/)
    .or(z.literal('')),
  bankCode: z
    .string()
    .length(4)
    .regex(/^\d{4}$/)
    .or(z.literal('')),
  holderName: z.string().max(255).or(z.literal('')),
  holderAddress: z.string().max(255).or(z.literal('')),
})

export const personalInfoSchema = z.object({
  titles: z.object({
    prefix: z.string().max(240).or(z.literal('')),
    suffix: z.string().max(240).or(z.literal('')),
  }),
  birthSurname: z.string().max(100).or(z.literal('')),
  maritalStatus: z.string().max(240).or(z.literal('')),
  birthPlace: z.object({
    country: z.string().max(70).min(1, 'Required'),
    city: z.string().max(75).or(z.literal('')),
  }),
})

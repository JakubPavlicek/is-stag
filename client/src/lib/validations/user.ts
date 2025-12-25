import { z } from 'zod'

export const contactSchema = z.object({
  email: z.string().email('validation.invalid_email').or(z.literal('')),
  phone: z
    .string()
    .max(20)
    .regex(/^\+?\d{1,20}$/, 'validation.invalid_phone')
    .or(z.literal('')),
  mobile: z
    .string()
    .max(30)
    .regex(/^\+?\d{1,30}$/, 'validation.invalid_mobile')
    .or(z.literal('')),
  dataBox: z
    .string()
    .length(7)
    .regex(/^[a-km-np-z2-9]{7}$/, 'validation.invalid_databox')
    .or(z.literal('')),
})

export const bankAccountSchema = z.object({
  accountNumberPrefix: z
    .string()
    .max(6)
    .regex(/^\d{1,6}$/, 'validation.digits_only')
    .or(z.literal('')),
  accountNumberSuffix: z
    .string()
    .max(10)
    .regex(/^\d{1,10}$/, 'validation.digits_only')
    .or(z.literal('')),
  bankCode: z
    .string()
    .length(4)
    .regex(/^\d{4}$/, 'validation.digits_only')
    .or(z.literal('')),
  holderName: z.string().max(255).or(z.literal('')),
  holderAddress: z.string().max(255).or(z.literal('')),
})

export const personalInfoSchema = z.object({
  titles: z.object({
    prefix: z.string().max(240).or(z.literal('')),
    suffix: z.string().max(240).or(z.literal('')),
  }),
  birthSurname: z
    .string()
    .max(100)
    .regex(/^[\p{L} .'-]+$/u, 'validation.invalid_surname')
    .or(z.literal('')),
  maritalStatus: z.string().max(240).or(z.literal('')),
  birthPlace: z.object({
    country: z.string().max(70).min(1),
    city: z.string().max(75).or(z.literal('')),
  }),
})

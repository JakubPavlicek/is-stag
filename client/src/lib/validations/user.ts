import { z } from 'zod'

/** Validation schema for user contact information. */
export const contactSchema = z.object({
  email: z.string().email('invalid_email').or(z.literal('')),
  phone: z
    .string()
    .max(20)
    .regex(/^\+?\d+$/, 'invalid_phone')
    .or(z.literal('')),
  mobile: z
    .string()
    .max(30)
    .regex(/^\+?\d+$/, 'invalid_mobile')
    .or(z.literal('')),
  dataBox: z
    .string()
    .length(7)
    .regex(/^[a-km-np-z2-9]{7}$/, 'invalid_databox')
    .or(z.literal('')),
})

/** Validation schema for bank account details. */
export const bankAccountSchema = z.object({
  accountNumberPrefix: z.string().max(6).regex(/^\d+$/, 'digits_only').or(z.literal('')),
  accountNumberSuffix: z.string().max(10).regex(/^\d+$/, 'digits_only').or(z.literal('')),
  bankCode: z.string().length(4).regex(/^\d+$/, 'digits_only').or(z.literal('')),
  holderName: z.string().max(255).or(z.literal('')),
  holderAddress: z.string().max(255).or(z.literal('')),
})

/** Validation schema for personal information. */
export const personalInfoSchema = z.object({
  titles: z.object({
    prefix: z.string().max(240).or(z.literal('')),
    suffix: z.string().max(240).or(z.literal('')),
  }),
  birthSurname: z
    .string()
    .max(100)
    .regex(/^[\p{L} .'-]+$/u, 'invalid_surname')
    .or(z.literal('')),
  maritalStatus: z.string().max(240).or(z.literal('')),
  birthPlace: z.object({
    country: z.string().max(70).or(z.literal('')),
    city: z.string().max(75).or(z.literal('')),
  }),
})

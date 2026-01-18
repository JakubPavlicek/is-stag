import { describe, expect, it } from 'vitest'

import { bankAccountSchema, contactSchema, personalInfoSchema } from '../validations/user'

describe('validations', () => {
  describe('contactSchema', () => {
    it('validates email', () => {
      expect(contactSchema.shape.email.safeParse('test@test.com').success).toBe(true)
      expect(contactSchema.shape.email.safeParse('invalid').success).toBe(false)
      expect(contactSchema.shape.email.safeParse('').success).toBe(true) // optional
    })

    it('validates phone', () => {
      expect(contactSchema.shape.phone.safeParse('+123456').success).toBe(true)
      expect(contactSchema.shape.phone.safeParse('abc').success).toBe(false)
    })

    it('validates dataBox', () => {
      expect(contactSchema.shape.dataBox.safeParse('abcdefg').success).toBe(true)
      expect(contactSchema.shape.dataBox.safeParse('short').success).toBe(false)
    })
  })

  describe('bankAccountSchema', () => {
    it('validates bankCode', () => {
      expect(bankAccountSchema.shape.bankCode.safeParse('0100').success).toBe(true)
      expect(bankAccountSchema.shape.bankCode.safeParse('123').success).toBe(false)
      expect(bankAccountSchema.shape.bankCode.safeParse('abc').success).toBe(false)
    })
  })

  describe('personalInfoSchema', () => {
    it('validates birthSurname', () => {
      expect(personalInfoSchema.shape.birthSurname.safeParse('Smith').success).toBe(true)
      expect(personalInfoSchema.shape.birthSurname.safeParse('123').success).toBe(false)
    })
  })
})

import { type ClassValue, clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatDate(
  date: string | number | Date,
  locale: string = 'cs',
  options?: Intl.DateTimeFormatOptions,
) {
  return new Intl.DateTimeFormat(locale, options).format(new Date(date))
}

import { type ClassValue, clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

/**
 * Merges Tailwind CSS classes with clsx logic.
 * - Handles conditional classes.
 * - Resolves conflicts using tailwind-merge.
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

/**
 * Formats a date string, number, or Date object according to the specified locale and options.
 * Defaults to Czech locale ('cs').
 */
export function formatDate(
  date: string | number | Date,
  locale: string = 'cs',
  options?: Intl.DateTimeFormatOptions,
) {
  // Use Intl.DateTimeFormat for locale-sensitive date formatting.
  // This ensures dates are displayed correctly for different regions (e.g. DD.MM.YYYY for 'cs').
  return new Intl.DateTimeFormat(locale, options).format(new Date(date))
}

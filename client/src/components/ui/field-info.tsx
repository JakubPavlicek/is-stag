import type { ReactNode } from 'react'
import { useTranslation } from 'react-i18next'

import type { ValidationError } from '@tanstack/react-form'

import { Label } from '@/components/ui/label'
import { cn } from '@/lib/utils'

interface MinimalFieldApi {
  name: string
  state: {
    meta: {
      isTouched: boolean
      errors: ValidationError[]
    }
  }
}

/**
 * Displays validation errors for a form field.
 * - Only shows errors if the field has been touched.
 * - Attempts to translate error messages using the 'zod' namespace.
 */
function FieldInfo({ field }: Readonly<{ field: MinimalFieldApi }>) {
  const { t, i18n } = useTranslation(['translation', 'zod'])

  return (
    <>
      {field.state.meta.isTouched && field.state.meta.errors.length ? (
        <p className="text-destructive text-[0.8rem] font-medium">
          {field.state.meta.errors
            .map((error) => {
              // Extract the message string from the error object
              const message =
                typeof error === 'object' && error && 'message' in error
                  ? (error as { message: string }).message
                  : error
              const msgString = message as string
              // Check if translation exists in 'zod' namespace, otherwise use raw message
              return i18n.exists(msgString, { ns: 'zod' }) ? t(msgString, { ns: 'zod' }) : msgString
            })
            .join(', ')}
        </p>
      ) : null}
    </>
  )
}

/**
 * Form field wrapper component.
 * - Combines a label, input (children), and error display.
 * - Highlights the label in red if there are validation errors.
 */
export function FormField({
  field,
  label,
  children,
  className,
}: Readonly<{
  field: MinimalFieldApi
  label: string
  children: ReactNode
  className?: string
}>) {
  return (
    <div className={cn('grid items-start gap-2', className)}>
      <Label
        htmlFor={field.name}
        className={field.state.meta.errors.length ? 'text-destructive' : ''}
      >
        {label}
      </Label>
      {children}
      <FieldInfo field={field} />
    </div>
  )
}

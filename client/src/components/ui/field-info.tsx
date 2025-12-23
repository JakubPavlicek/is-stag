import type { ReactNode } from 'react'

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

function FieldInfo({ field }: Readonly<{ field: MinimalFieldApi }>) {
  return (
    <>
      {field.state.meta.isTouched && field.state.meta.errors.length ? (
        <p className="text-destructive text-[0.8rem] font-medium">
          {field.state.meta.errors.map((error) => error?.toString()).join(', ')}
        </p>
      ) : null}
    </>
  )
}

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
    <div className={cn('grid gap-2', className)}>
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

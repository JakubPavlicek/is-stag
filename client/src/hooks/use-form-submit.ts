import { useTranslation } from 'react-i18next'

import { useQueryClient } from '@tanstack/react-query'
import { useRouter } from '@tanstack/react-router'
import { toast } from 'sonner'

type SubmitOptions<TData, TError, TVariables> = {
  mutationFn: (variables: TVariables) => Promise<{ data?: TData; error?: TError } | undefined>
  onSuccess?: (data: TData) => Promise<void> | void
  invalidateKeys?: string[][]
  successMessage?: string
}

export function useFormSubmit() {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const router = useRouter()

  const handleSubmit = async <TData, TError, TVariables>(
    variables: TVariables,
    {
      mutationFn,
      onSuccess,
      invalidateKeys = [],
      successMessage,
    }: SubmitOptions<TData, TError, TVariables>,
  ) => {
    const promise = mutationFn(variables).then(async (res) => {
      // 204 No Content often returns undefined or null, which is a success.
      // We only throw if we explicitly get an error object.
      if (res?.error) {
        throw res.error
      }

      if (invalidateKeys.length > 0) {
        await Promise.all(
          invalidateKeys.map((key) =>
            queryClient.invalidateQueries({
              queryKey: key,
              refetchType: 'inactive',
            }),
          ),
        )
      }

      await router.invalidate()

      if (onSuccess) {
        await onSuccess(res?.data as TData)
      }

      return res?.data
    })

    toast.promise(promise, {
      loading: t('saving'),
      success: successMessage || t('saved_successfully'),
      error: (error) => {
        console.error(error)
        return t('error_occured')
      },
    })

    return promise
  }

  return { handleSubmit }
}

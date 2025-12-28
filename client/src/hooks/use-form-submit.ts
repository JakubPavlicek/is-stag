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

/**
 * A custom hook to handle form submissions with standardized feedback and cache invalidation.
 *
 * Wraps the mutation logic in a toast promise for better UX.
 * - Handles success (invalidates queries, router, shows success toast).
 * - Handles errors (shows error toast, logs error).
 */
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
        // Invalidate specific query keys to ensure data consistency across the app.
        // Using 'inactive' refetchType allows queries to be marked as stale without forcing an immediate refetching if they aren't currently visible.
        await Promise.all(
          invalidateKeys.map((key) =>
            queryClient.invalidateQueries({
              queryKey: key,
              refetchType: 'inactive',
            }),
          ),
        )
      }

      // Invalidate the router context to trigger a reload of route loaders, ensuring the UI reflects the latest server state.
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

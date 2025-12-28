import { Moon, Sun } from 'lucide-react'

import { useTheme } from '@/components/theme-provider'
import { Button } from '@/components/ui/button'

/** A toggle button to switch between light and dark themes. */
export function ModeToggle() {
  const { theme, setTheme } = useTheme()

  const toggleTheme = () => {
    // Determine the current effective theme to handle 'system' correctly.
    // If the theme is explicitly 'dark' or if it is 'system' and the OS preference is dark, we consider the current state as dark.
    const isDark =
      theme === 'dark' ||
      (theme === 'system' && globalThis.matchMedia('(prefers-color-scheme: dark)').matches)

    // Toggle to the opposite theme
    setTheme(isDark ? 'light' : 'dark')
  }

  return (
    <Button variant="ghost" size="icon" onClick={toggleTheme}>
      <Moon className="h-[1.2rem] w-[1.2rem] scale-100 rotate-0 transition-all dark:scale-0 dark:rotate-90" />
      <Sun className="absolute h-[1.2rem] w-[1.2rem] scale-0 -rotate-90 transition-all dark:scale-100 dark:rotate-0" />
      <span className="sr-only">Toggle theme</span>
    </Button>
  )
}

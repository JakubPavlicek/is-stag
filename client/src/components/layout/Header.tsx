import { useState } from 'react'
import { useTranslation } from 'react-i18next'

import { useKeycloak } from '@react-keycloak/web'
import { CZ, GB } from 'country-flag-icons/react/3x2'
import { Globe, LogOut, Menu } from 'lucide-react'

import { ModeToggle } from '@/components/theme/mode-toggle'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Sheet, SheetContent, SheetTitle, SheetTrigger } from '@/components/ui/sheet'

import { Sidebar } from './Sidebar'

/**
 * Global application header.
 * - Contains the mobile sidebar toggle.
 * - Theme toggle (ModeToggle).
 * - Language switcher (Globe icon).
 * - User profile dropdown (Login/Logout, User info).
 */
export function Header() {
  const { t, i18n } = useTranslation()
  const { keycloak } = useKeycloak()
  const [isOpen, setIsOpen] = useState(false)

  // Switch i18n language dynamically
  const changeLanguage = (lng: string) => {
    i18n.changeLanguage(lng)
  }

  // Extract user initials for the avatar fallback (e.g. "John Doe" -> "JD")
  const userInitials = keycloak.tokenParsed?.name?.substring(0, 2).toUpperCase() || 'U'

  return (
    <header className="bg-background flex h-16 items-center gap-4 border-b px-6 shadow-sm">
      <div className="lg:hidden">
        <Sheet open={isOpen} onOpenChange={setIsOpen}>
          <SheetTrigger asChild>
            <Button variant="outline" size="icon" className="hover:bg-accent transition-colors">
              <Menu className="h-5 w-5" />
            </Button>
          </SheetTrigger>
          <SheetContent side="left" className="w-60 p-0" aria-describedby={undefined}>
            <SheetTitle className="sr-only">Menu</SheetTitle>
            <Sidebar className="w-full border-none" onNavigate={() => setIsOpen(false)} />
          </SheetContent>
        </Sheet>
      </div>

      <div className="flex-1" />

      <div className="mr-2 flex items-center gap-2">
        <ModeToggle />

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="ghost"
              size="icon"
              className="hover:bg-accent hover:text-accent-foreground transition-all duration-200 hover:scale-105 active:scale-95"
              aria-label="Switch language"
            >
              <Globe className="h-5 w-5" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-40">
            <DropdownMenuItem
              onClick={() => changeLanguage('cs')}
              className="flex cursor-pointer items-center gap-2"
            >
              <CZ title="Čeština" className="h-4 w-6 rounded-sm shadow-sm" />
              <span>Čeština</span>
            </DropdownMenuItem>
            <DropdownMenuItem
              onClick={() => changeLanguage('en')}
              className="flex cursor-pointer items-center gap-2"
            >
              <GB title="English" className="h-4 w-6 rounded-sm shadow-sm" />
              <span>English</span>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      {keycloak.authenticated ? (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="ghost"
              className="hover:bg-accent relative h-9 w-9 rounded-full transition-transform hover:scale-105"
            >
              <Avatar className="h-9 w-9 border shadow-sm">
                <AvatarImage src="/avatars/01.png" alt="User" />
                <AvatarFallback className="bg-primary/10 text-primary font-semibold">
                  {userInitials}
                </AvatarFallback>
              </Avatar>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-56">
            <DropdownMenuLabel className="font-semibold">
              {keycloak.tokenParsed?.name || keycloak.tokenParsed?.preferred_username}
            </DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem
              onClick={() => keycloak.logout()}
              className="text-destructive focus:bg-destructive/10 focus:text-destructive cursor-pointer"
            >
              <LogOut className="mr-0.5 h-4 w-4" />
              {t('logout')}
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      ) : (
        <Button
          onClick={() => keycloak.login()}
          className="shadow-primary/20 transition-all hover:scale-105 hover:shadow-md active:scale-95"
        >
          {t('login')}
        </Button>
      )}
    </header>
  )
}

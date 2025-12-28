import * as React from "react"
import { Check, ChevronsUpDown, X } from "lucide-react"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"

export interface ComboboxOption {
  value: string
  label: string
}

interface ComboboxProps {
  options: ComboboxOption[]
  value?: string
  onSelect: (value: string) => void
  placeholder?: string
  emptyText?: string
  className?: string
  modal?: boolean
}

/**
 * Searchable dropdown/combobox component.
 * - Allows users to search and select from a list of options.
 * - Supports clearing the selection via an 'X' icon.
 * - Built on top of Command and Popover primitives.
 */
export function Combobox({
  options,
  value,
  onSelect,
  placeholder = "Select option...",
  emptyText = "No option found.",
  className,
  modal = false,
}: Readonly<ComboboxProps>) {
  const [open, setOpen] = React.useState(false)

  return (
    <Popover open={open} onOpenChange={setOpen} modal={modal}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          aria-expanded={open}
          className={cn("h-auto w-full justify-between min-w-0 shadow-sm", !value && "text-muted-foreground", className)}
        >
          <span className="flex-1 truncate text-left">
            {value
              ? options.find((option) => option.value === value)?.label
              : placeholder}
          </span>
          <div className="ml-2 flex items-center gap-1">
            {/* Clear button - only shown when a value is selected */}
            {value && (
              <div
                role="button"
                tabIndex={0}
                onClick={(e) => {
                  e.stopPropagation()
                  onSelect("")
                }}
                onKeyDown={(e) => {
                  if (e.key === "Enter" || e.key === " ") {
                    e.stopPropagation()
                    onSelect("")
                  }
                }}
                className="flex h-4 w-4 shrink-0 cursor-pointer items-center justify-center opacity-50 hover:opacity-100"
              >
                <X className="h-4 w-4" />
              </div>
            )}
            <ChevronsUpDown className="h-4 w-4 shrink-0 opacity-50" />
          </div>
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-75 p-0" align="start">
        <Command
          filter={(value, search) => {
            // Custom filter: case-insensitive substring match
            if (value.toLowerCase().includes(search.toLowerCase())) return 1
            return 0
          }}
        >
          <CommandInput placeholder={placeholder} />
          <CommandList>
            <CommandEmpty>{emptyText}</CommandEmpty>
            <CommandGroup>
              {options.map((option) => (
                <CommandItem
                  key={option.value}
                  value={option.label}
                  onSelect={() => {
                    // Toggle selection: deselect if already selected, otherwise select
                    onSelect(option.value === value ? "" : option.value)
                    setOpen(false)
                  }}
                  className="items-start"
                >
                  <Check
                    className={cn(
                      "mr-2 h-4 w-4 mt-0.5",
                      value === option.value ? "opacity-100" : "opacity-0"
                    )}
                  />
                  <span className="flex-1 text-left">{option.label}</span>
                </CommandItem>
              ))}
            </CommandGroup>
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  )
}

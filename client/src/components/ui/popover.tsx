import * as React from 'react'

import * as PopoverPrimitive from '@radix-ui/react-popover'

import { cn } from '@/lib/utils'

/**
 * Popover root component - wraps Radix UI Popover.Root.
 * - Manages the open/closed state of the popover.
 */
const Popover = PopoverPrimitive.Root

/** Popover trigger - element that opens the popover when clicked. */
const PopoverTrigger = PopoverPrimitive.Trigger

/**
 * Popover content container.
 * - Positioned relative to the trigger element.
 * - Rendered in a portal with animated entrance/exit.
 * - Used for tooltips, dropdowns, and contextual menus.
 */
const PopoverContent = React.forwardRef<
  React.ComponentRef<typeof PopoverPrimitive.Content>,
  React.ComponentPropsWithoutRef<typeof PopoverPrimitive.Content>
>(({ className, align = 'center', sideOffset = 4, ...props }, ref) => (
  <PopoverPrimitive.Portal>
    <PopoverPrimitive.Content
      ref={ref}
      align={align}
      sideOffset={sideOffset}
      className={cn(
        'bg-popover text-popover-foreground data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95 data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2 z-50 w-72 rounded-md border p-4 shadow-md outline-none',
        className,
      )}
      {...props}
    />
  </PopoverPrimitive.Portal>
))
PopoverContent.displayName = PopoverPrimitive.Content.displayName

export { Popover, PopoverTrigger, PopoverContent }

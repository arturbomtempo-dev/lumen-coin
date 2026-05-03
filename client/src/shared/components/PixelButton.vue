<script setup lang="ts">
type Variant = 'primary' | 'secondary' | 'success' | 'info' | 'accent' | 'ghost' | 'danger'
type Size = 'sm' | 'md' | 'lg'

withDefaults(defineProps<{
  variant?: Variant
  size?: Size
  type?: 'button' | 'submit' | 'reset'
  disabled?: boolean
}>(), {
  variant: 'primary',
  size: 'md',
  type: 'button',
  disabled: false,
})

const variantClass: Record<Variant, string> = {
  primary: 'bg-primary text-primary-foreground [--btn-shadow:hsl(var(--primary-shadow))]',
  secondary: 'bg-secondary text-secondary-foreground [--btn-shadow:hsl(var(--secondary-shadow))]',
  success: 'bg-success text-success-foreground [--btn-shadow:hsl(var(--success-shadow))]',
  info: 'bg-info text-info-foreground [--btn-shadow:hsl(var(--info-shadow))]',
  accent: 'bg-accent text-accent-foreground [--btn-shadow:hsl(var(--accent-shadow))]',
  ghost: 'bg-card text-card-foreground [--btn-shadow:hsl(var(--muted))]',
  danger: 'bg-destructive text-destructive-foreground [--btn-shadow:hsl(var(--border))]',
}

const sizeClass: Record<Size, string> = {
  sm: 'px-3 py-1.5 text-[10px]',
  md: 'px-5 py-2.5 text-xs',
  lg: 'px-7 py-3.5 text-sm',
}
</script>

<template>
  <button
    :type="type"
    :disabled="disabled"
    :class="[
      'relative inline-flex items-center justify-center gap-2 font-pixel uppercase select-none',
      'border-2 border-border',
      'transition-all duration-75 active:translate-x-[3px] active:translate-y-[3px]',
      'shadow-[3px_3px_0_0_var(--btn-shadow,hsl(var(--border)))] active:shadow-none',
      'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background',
      'disabled:opacity-60 disabled:pointer-events-none',
      variantClass[variant],
      sizeClass[size],
    ]"
  >
    <slot />
  </button>
</template>

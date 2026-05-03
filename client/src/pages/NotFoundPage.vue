<script setup lang="ts">
import { RouterLink } from 'vue-router'
import PixelButton from '@/shared/components/PixelButton.vue'
</script>

<template>
  <div class="min-h-screen flex flex-col items-center justify-center p-4 text-center">
    <div class="font-pixel text-6xl md:text-8xl text-primary animate-glow">404</div>
    <div class="font-pixel text-xl mt-4">GAME OVER</div>
    <p class="font-sans text-muted-foreground mt-3 max-w-xs">Esta fase não existe. Você caiu em um warp zone errado!</p>
    <div class="mt-8">
      <RouterLink to="/">
        <PixelButton variant="primary">▶ VOLTAR AO MAPA</PixelButton>
      </RouterLink>
    </div>
  </div>
</template>

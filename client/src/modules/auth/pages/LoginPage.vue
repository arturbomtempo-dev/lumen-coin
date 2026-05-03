<script setup lang="ts">
import { ref } from 'vue';
import { RouterLink } from 'vue-router';
import {
    PhArrowLeft,
    PhGameController,
    PhSun,
    PhMoon,
    PhSparkle,
    PhEye,
    PhEyeSlash,
} from '@phosphor-icons/vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import { useThemeStore } from '@/shared/stores/theme.store';
import Cloud from './_components/Cloud.vue';

const themeStore = useThemeStore();

const email = ref('');
const senha = ref('');
const showPwd = ref(false);

</script>

<template>
    <div class="min-h-screen flex flex-col">

        <main class="flex-1 relative overflow-hidden bg-login-bg transition-colors duration-300">
            <div class="absolute inset-0 bg-info/10" />
            <!-- Clouds -->
               <div class="absolute top-10 left-8">
                 <Cloud />
                </div>

                <div class="absolute top-24 right-16 opacity-80">
                    <Cloud />
                </div>

                <div class="absolute top-40 left-1/3 opacity-70">
                    <Cloud />
                </div>
            <!-- Hills -->
            <div
                class="absolute bottom-24 left-0 right-0 h-32 opacity-30"
                :style="{
                    backgroundImage:
                        'radial-gradient(circle at 15% 100%, hsl(var(--success)) 0 80px, transparent 81px), radial-gradient(circle at 65% 100%, hsl(var(--success)) 0 100px, transparent 101px)',
                }"
            />
            <!-- Brick floor -->
            <div
                class="absolute bottom-0 left-0 right-0 h-24 border-t-4 border-border bg-secondary"
                :style="{
                    backgroundImage:
                        'repeating-linear-gradient(0deg, hsl(var(--secondary-shadow)) 0 2px, transparent 2px 24px), repeating-linear-gradient(90deg, hsl(var(--secondary-shadow)) 0 2px, transparent 2px 48px)',
                }"
            />
            <!-- Question block -->
            <div class="absolute bottom-32 right-10 hidden md:block">
                <div
                    class="w-16 h-16 bg-primary border-4 border-border shadow-[4px_4px_0_0_hsl(var(--border))] flex items-center justify-center font-pixel text-2xl text-primary-foreground animate-bob"
                >
                    ?
                </div>
            </div>
            
            <!-- Pipe -->
            <div class="absolute bottom-24 left-10 hidden md:block">
                <div class="w-20 h-8 bg-success border-4 border-border" />
                <div class="w-16 h-16 bg-success border-4 border-t-0 border-border ml-2" />
            </div>
            <!-- Mario -->
             <div class="absolute bottom-24 right-1/4 block animate-bob z-0">
                <MarioAvatar character="mario" :size="72" />
            </div>
            

            <div class="relative min-h-screen container z-10  py-10 flex items-center justify-center">
                <PixelCard class="w-full max-w-md p-6 md:p-8 space-y-5 crt-scanlines">
                    <!-- VOLTAR -->
        <RouterLink to="/"
          class="mb-5 flex items-center gap-3 font-pixel text-[9px] cursor-pointer transition-all"
        >
          <div
            class="w-7 h-7 border-2 border-black bg-amber-300 flex items-center justify-center shadow-[2px_2px_0px_black] translate-y-0 hover:translate-y-0.5 hover:translate-x-0.5 hover:shadow-none transition-transform"
          >
            <PhArrowLeft :size="14" weight="bold" />
          </div>

          <span class="hover:text-primary">VOLTAR À TELA INICIAL</span>
        </RouterLink>
                    <div class="flex items-center gap-3 mb-5">
                        
                        <div>
                            
                            <h1 class="font-pixel text-lg">ENTRAR</h1>
                        </div>
                        <div class="ml-auto animate-bob"><CoinIcon :size="28" class="animate-bob" /></div>
                    </div>

                   

                    <form class="space-y-4">
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">E-MAIL</label>
                            <PixelInput
                                v-model="email"
                                type="email"
                                placeholder="voce@email.com"
                                required
                            />
                        </div>
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">SENHA</label>
                            <div class="relative">
                                <PixelInput
                                    v-model="senha"
                                    :type="showPwd ? 'text' : 'password'"
                                    placeholder="••••••••"
                                    required
                                />
                                <button
                                    type="button"
                                    class="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                                    aria-label="Mostrar senha"
                                    @click="showPwd = !showPwd"
                                >
                                    <PhEyeSlash v-if="showPwd" weight="bold" />
                                    <PhEye v-else weight="bold" />
                                </button>
                            </div>
                        </div>

                        <div class="flex items-center justify-between font-pixel text-[9px]">
                            <label class="flex items-center gap-2">
                                <input type="checkbox" class="accent-primary" /> LEMBRAR
                            </label>
                            <a href="#" class="hover:text-primary">ESQUECI A SENHA</a>
                        </div>

                        <PixelButton variant="primary" class="w-full cursor-pointer" type="submit">
                            <PhSparkle weight="fill" class="pixel-icon cursor-pointer" /> PRESS START
                        </PixelButton>
                    </form>

                    <div
                        class="my-5 flex items-center gap-2 font-pixel text-[9px] text-muted-foreground"
                    >
                        <div class="flex-1 h-px bg-border" />
                        OU
                        <div class="flex-1 h-px bg-border" />
                    </div>

                    <RouterLink to="/signup">
                        <PixelButton variant="success" class="w-full"
                            >CRIAR PERSONAGEM</PixelButton
                        >
                    </RouterLink>

                    
                </PixelCard>
            </div>
        </main>
    </div>
</template>

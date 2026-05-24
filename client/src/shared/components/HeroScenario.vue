<template>
    <div
        class="relative w-full max-w-sm md:max-w-2xl mx-auto mt-10 bg-login-bg transition-colors duration-300 min-h-74 overflow-hidden border-3 border-border"
    >
        <div class="relative h-48 md:mt-15">
            <div class="absolute -bottom-18 sm:-bottom-4 left-8 flex flex-col items-center">
                <div
                    class="cursor-pointer"
                    :style="
                        isJumping
                            ? 'transform: translateY(-40px); transition: transform 450ms ease-out;'
                            : 'transform: translateY(0); transition: transform 350ms ease-in;'
                    "
                >
                    <CharacterAvatar character="mario" :size="80" />
                </div>
            </div>

            <div class="absolute -bottom-20 sm:-bottom-5 right-8 flex flex-col items-center">
                <CharacterAvatar character="luigi" :size="80" class="animate-bounce" />
            </div>
        </div>

        <div class="absolute bottom-36 left-11 flex flex-col items-center">
            <div class="relative w-14">
                <div v-if="coinVisible" class="absolute -top-10 left-0 right-0 flex justify-center">
                    <CoinIcon :size="32" class="animate-bounce" />
                </div>
                <div
                    class="w-14 h-14 bg-primary border-4 border-border flex items-center justify-center font-pixel text-xl"
                >
                    ?
                </div>
            </div>
        </div>

        <div class="absolute bottom-6 left-1/2 -translate-x-1/2 flex items-end">
            <div class="flex flex-col items-center">
                <div class="w-20 h-6 bg-success border-4 border-border rounded-t-sm" />
                <div class="w-16 h-16 bg-success border-4 border-t-0 border-border" />
            </div>
        </div>

        <div class="absolute bottom-26 left-1/2 -translate-x-1/2 flex flex-col items-center">
            <CharacterAvatar character="yoshi" :size="88" class="animate-bounce" />
        </div>

        <div class="absolute bottom-0 left-0 right-0 h-6 bg-red-800 border-t-4 border-border" />

        <div class="absolute top-8 right-12">
            <Cloud />
        </div>
    </div>
</template>

<script setup lang="ts">
import Cloud from '@/modules/auth/pages/_components/Cloud.vue';
import CharacterAvatar from '@/shared/components/CharacterAvatar.vue';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import { onMounted, onUnmounted, ref } from 'vue';

const isJumping = ref(false);
const coinVisible = ref(false);
let autoInterval: number | undefined;

function jump() {
    if (isJumping.value) return;
    isJumping.value = true;

    setTimeout(() => {
        coinVisible.value = true;
    }, 380);

    setTimeout(() => {
        coinVisible.value = false;
    }, 1400);

    setTimeout(() => {
        isJumping.value = false;
    }, 900);
}

onMounted(() => {
    autoInterval = window.setInterval(() => {
        jump();
    }, 2000);
});

onUnmounted(() => {
    if (autoInterval) clearInterval(autoInterval);
});
</script>

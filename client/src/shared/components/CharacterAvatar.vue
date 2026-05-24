<script setup lang="ts">
import Bowser from '@/shared/components/avatars/Bowser.vue';
import Company from '@/shared/components/avatars/Company.vue';
import Institution from '@/shared/components/avatars/Institution.vue';
import Luigi from '@/shared/components/avatars/Luigi.vue';
import Mario from '@/shared/components/avatars/Mario.vue';
import Peach from '@/shared/components/avatars/Peach.vue';
import Toad from '@/shared/components/avatars/Toad.vue';
import Yoshi from '@/shared/components/avatars/Yoshi.vue';
import type { MarioCharacter } from '@/shared/data/characters';
import { computed } from 'vue';

const props = withDefaults(
    defineProps<{
        character?: MarioCharacter;
        size?: number;
    }>(),
    {
        character: 'mario',
        size: 64,
    }
);

const avatarMap = {
    mario: Mario,
    luigi: Luigi,
    peach: Peach,
    toad: Toad,
    yoshi: Yoshi,
    bowser: Bowser,
    institution: Institution,
    company: Company,
};

const currentAvatar = computed(() => avatarMap[props.character]);
</script>

<template>
    <component :is="currentAvatar" :size="size" />
</template>

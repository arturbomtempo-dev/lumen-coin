<script setup lang="ts">
import { computed, toRefs } from 'vue';
import type { MarioCharacter } from '@/shared/data/characters';

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

const { character, size } = toRefs(props);

const avatarSrc = computed(() => {
    if (character.value === 'mario') return '/mario.png';
    if (character.value === 'luigi') return '/luigi.png';
    if (character.value === 'peach') return '/princess-peach.png';

    return '';
});

const palettes: Record<MarioCharacter, Record<string, string>> = {
    mario: {
        hat: '#E52521',
        shirt: '#E52521',
        overall: '#1F4FD8',
        skin: '#FBC8A0',
        hair: '#3A1F0B',
        shoe: '#5B2B0E',
    },
    luigi: {
        hat: '#39B54A',
        shirt: '#39B54A',
        overall: '#1F4FD8',
        skin: '#FBC8A0',
        hair: '#3A1F0B',
        shoe: '#5B2B0E',
    },
    peach: {
        hat: '#F4C7E1',
        shirt: '#FF8AC2',
        overall: '#FF8AC2',
        skin: '#FBD9B5',
        hair: '#F0C040',
        shoe: '#C03070',
    },
    toad: {
        hat: '#FFFFFF',
        shirt: '#FFFFFF',
        overall: '#1F4FD8',
        skin: '#FBE2C0',
        hair: '#FFFFFF',
        shoe: '#5B2B0E',
        spot: '#E52521',
    },
    yoshi: {
        hat: '#39B54A',
        shirt: '#39B54A',
        overall: '#FFFFFF',
        skin: '#FBD9B5',
        hair: '#39B54A',
        shoe: '#E2622E',
        spot: '#1F4FD8',
    },
    bowser: {
        hat: '#39B54A',
        shirt: '#F2A93B',
        overall: '#39B54A',
        skin: '#F2A93B',
        hair: '#E52521',
        shoe: '#1F1F1F',
        spot: '#FFFFFF',
    },
    institution: {
        hat: '#C0392B',

        shirt: '#1E4D32',

        overall: '#B68252',

        skin: '#66B97A',

        hair: '#6B3F1F',

        shoe: '#3B2411',

        spot: '#F4F1DC',
    },
    company: {
        hat: '#2563EB',
        shirt: '#1D4ED8',
        overall: '#E5E7EB',
        skin: '#93C5FD',
        hair: '#1E3A8A',
        shoe: '#111827',
        spot: '#F8FAFC',
    },
};
</script>

<template>
    <img
        v-if="avatarSrc"
        :src="avatarSrc"
        :alt="character"
        :width="size"
        :height="size"
        class="block object-contain"
        :style="{ width: `${size}px`, height: `${size}px`, imageRendering: 'pixelated' }"
    />

    <svg
        v-else
        :width="size"
        :height="size"
        viewBox="0 0 12 13"
        shape-rendering="crispEdges"
        :style="{ imageRendering: 'pixelated' }"
        :aria-label="character"
    >
        <rect x="0" y="0" width="12" height="13" fill="transparent" />

        <template v-if="character === 'peach'">
            <rect x="4" y="0" width="1" height="1" fill="#F8D000" />
            <rect x="6" y="0" width="1" height="1" fill="#F8D000" />
            <rect x="8" y="0" width="1" height="1" fill="#F8D000" />
            <rect x="4" y="1" width="5" height="1" fill="#F8D000" />
            <rect x="3" y="2" width="6" height="1" :fill="palettes.peach.hair" />
            <rect x="2" y="3" width="2" height="4" :fill="palettes.peach.hair" />
            <rect x="8" y="3" width="2" height="4" :fill="palettes.peach.hair" />
            <rect x="2" y="7" width="1" height="1" :fill="palettes.peach.hair" />
            <rect x="9" y="7" width="1" height="1" :fill="palettes.peach.hair" />
            <rect x="4" y="3" width="4" height="3" :fill="palettes.peach.skin" />
            <rect x="3" y="4" width="1" height="2" :fill="palettes.peach.skin" />
            <rect x="8" y="4" width="1" height="2" :fill="palettes.peach.skin" />
            <rect x="4" y="4" width="1" height="2" fill="hsl(var(--border))" />
            <rect x="7" y="4" width="1" height="2" fill="hsl(var(--border))" />
            <rect x="5" y="6" width="2" height="1" fill="#C03070" />
            <rect x="2" y="5" width="1" height="1" fill="#F8D000" />
            <rect x="9" y="5" width="1" height="1" fill="#F8D000" />
            <rect x="3" y="7" width="6" height="1" :fill="palettes.peach.shirt" />
            <rect x="2" y="8" width="1" height="1" :fill="palettes.peach.shirt" />
            <rect x="9" y="8" width="1" height="1" :fill="palettes.peach.shirt" />
            <rect x="3" y="8" width="6" height="1" :fill="palettes.peach.shirt" />
            <rect x="2" y="9" width="8" height="1" :fill="palettes.peach.shirt" />
            <rect x="1" y="10" width="10" height="1" :fill="palettes.peach.shirt" />
            <rect x="5" y="7" width="2" height="1" fill="#1F4FD8" />
            <rect x="3" y="11" width="2" height="1" :fill="palettes.peach.shoe" />
            <rect x="7" y="11" width="2" height="1" :fill="palettes.peach.shoe" />
        </template>

        <template v-else-if="character === 'toad'">
            <rect x="2" y="0" width="8" height="3" :fill="palettes.toad.hat" />
            <rect x="1" y="1" width="1" height="2" :fill="palettes.toad.hat" />
            <rect x="10" y="1" width="1" height="2" :fill="palettes.toad.hat" />
            <rect x="3" y="1" width="2" height="1" :fill="palettes.toad.spot" />
            <rect x="7" y="1" width="2" height="1" :fill="palettes.toad.spot" />
            <rect x="5" y="2" width="2" height="1" :fill="palettes.toad.spot" />
            <rect x="3" y="3" width="6" height="3" :fill="palettes.toad.skin" />
            <rect x="4" y="4" width="1" height="1" fill="hsl(var(--border))" />
            <rect x="7" y="4" width="1" height="1" fill="hsl(var(--border))" />
            <rect x="5" y="5" width="2" height="1" fill="#D04060" />
            <rect x="3" y="6" width="6" height="3" :fill="palettes.toad.shirt" />
            <rect x="4" y="7" width="4" height="1" :fill="palettes.toad.overall" />
            <rect x="3" y="9" width="3" height="2" :fill="palettes.toad.overall" />
            <rect x="6" y="9" width="3" height="2" :fill="palettes.toad.overall" />
            <rect x="3" y="11" width="3" height="1" :fill="palettes.toad.shoe" />
            <rect x="6" y="11" width="3" height="1" :fill="palettes.toad.shoe" />
        </template>

        <template v-else-if="character === 'yoshi'">
            <rect x="2" y="1" width="8" height="4" :fill="palettes.yoshi.hat" />
            <rect x="3" y="0" width="2" height="1" :fill="palettes.yoshi.hat" />
            <rect x="4" y="2" width="2" height="2" fill="#FFFFFF" />
            <rect x="7" y="2" width="2" height="2" fill="#FFFFFF" />
            <rect x="5" y="3" width="1" height="1" fill="hsl(var(--border))" />
            <rect x="8" y="3" width="1" height="1" fill="hsl(var(--border))" />
            <rect x="2" y="4" width="3" height="2" :fill="palettes.yoshi.skin" />
            <rect x="3" y="6" width="2" height="1" :fill="palettes.yoshi.skin" />
            <rect x="3" y="6" width="6" height="3" :fill="palettes.yoshi.hat" />
            <rect x="4" y="7" width="4" height="2" :fill="palettes.yoshi.overall" />
            <rect x="3" y="9" width="3" height="2" :fill="palettes.yoshi.shoe" />
            <rect x="6" y="9" width="3" height="2" :fill="palettes.yoshi.shoe" />
            <rect x="9" y="5" width="1" height="1" :fill="palettes.yoshi.spot" />
            <rect x="10" y="6" width="1" height="1" :fill="palettes.yoshi.spot" />
        </template>

        <template v-else-if="character === 'bowser'">
            <rect x="2" y="0" width="2" height="2" :fill="palettes.bowser.spot" />
            <rect x="8" y="0" width="2" height="2" :fill="palettes.bowser.spot" />
            <rect x="4" y="0" width="4" height="1" :fill="palettes.bowser.hair" />
            <rect x="3" y="1" width="6" height="4" :fill="palettes.bowser.hat" />
            <rect x="4" y="3" width="4" height="2" :fill="palettes.bowser.skin" />
            <rect x="4" y="2" width="1" height="1" fill="hsl(var(--border))" />
            <rect x="7" y="2" width="1" height="1" fill="hsl(var(--border))" />
            <rect x="2" y="5" width="8" height="5" :fill="palettes.bowser.shirt" />
            <rect x="3" y="6" width="6" height="3" :fill="palettes.bowser.hat" />
            <rect x="5" y="7" width="2" height="1" :fill="palettes.bowser.spot" />
            <rect x="2" y="10" width="3" height="2" :fill="palettes.bowser.shoe" />
            <rect x="7" y="10" width="3" height="2" :fill="palettes.bowser.shoe" />
        </template>

        <template v-else-if="character === 'institution'">
            <rect x="1" y="1" width="10" height="9" :fill="palettes.institution.hair" />

            <rect x="2" y="2" width="8" height="7" :fill="palettes.institution.shirt" />

            <rect x="2" y="9" width="8" height="1" :fill="palettes.institution.overall" />

            <rect x="0" y="11" width="12" height="1" :fill="palettes.institution.overall" />

            <text
                x="6"
                y="6"
                text-anchor="middle"
                dominant-baseline="middle"
                font-size="3"
                font-weight="bold"
                fill="#FFFFFF"
                style="font-family: monospace"
            >
                A+
            </text>
        </template>

        <template v-else-if="character === 'company'">
            <rect x="2" y="0" width="8" height="2" :fill="palettes.company.hat" />
            <rect x="2" y="2" width="8" height="8" :fill="palettes.company.shirt" />
            <rect x="5" y="7" width="2" height="3" :fill="palettes.company.shoe" />
            <rect x="3" y="3" width="1" height="1" :fill="palettes.company.spot" />
            <rect x="5" y="3" width="1" height="1" :fill="palettes.company.spot" />
            <rect x="7" y="3" width="1" height="1" :fill="palettes.company.spot" />
            <rect x="3" y="5" width="1" height="1" :fill="palettes.company.spot" />
            <rect x="5" y="5" width="1" height="1" :fill="palettes.company.spot" />
            <rect x="7" y="5" width="1" height="1" :fill="palettes.company.spot" />
            <rect x="1" y="10" width="10" height="1" :fill="palettes.company.overall" />
            <rect x="0" y="11" width="12" height="1" :fill="palettes.company.shoe" />
        </template>

        <template v-else>
            <rect x="3" y="0" width="6" height="1" :fill="palettes[character].hat" />
            <rect x="2" y="1" width="2" height="1" :fill="palettes[character].hat" />
            <rect x="4" y="1" width="5" height="1" :fill="palettes[character].hat" />
            <rect x="5" y="2" width="2" height="1" fill="#FFFFFF" />
            <rect x="2" y="2" width="2" height="2" :fill="palettes[character].hair" />
            <rect x="8" y="2" width="2" height="2" :fill="palettes[character].hair" />
            <rect x="4" y="2" width="1" height="1" :fill="palettes[character].skin" />
            <rect x="7" y="2" width="1" height="1" :fill="palettes[character].skin" />
            <rect x="3" y="3" width="6" height="3" :fill="palettes[character].skin" />
            <rect x="4" y="4" width="1" height="1" fill="hsl(var(--border))" />
            <rect x="7" y="4" width="1" height="1" fill="hsl(var(--border))" />
            <rect x="3" y="5" width="6" height="1" :fill="palettes[character].hair" />
            <rect x="5" y="5" width="2" height="1" fill="#D06060" />
            <rect x="2" y="6" width="8" height="2" :fill="palettes[character].shirt" />
            <rect x="3" y="8" width="6" height="3" :fill="palettes[character].overall" />
            <rect x="5" y="8" width="2" height="1" :fill="palettes[character].shirt" />
            <rect x="4" y="9" width="1" height="1" fill="#F8D000" />
            <rect x="7" y="9" width="1" height="1" fill="#F8D000" />
            <rect x="2" y="11" width="3" height="1" :fill="palettes[character].shoe" />
            <rect x="7" y="11" width="3" height="1" :fill="palettes[character].shoe" />
        </template>
    </svg>
</template>

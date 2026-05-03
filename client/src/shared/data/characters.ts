export type MarioCharacter = 'mario' | 'luigi' | 'peach' | 'toad' | 'yoshi' | 'bowser';

export const MARIO_CHARACTERS: { id: MarioCharacter; nome: string; classe: string }[] = [
    { id: 'mario', nome: 'Mario', classe: 'Encanador Herói' },
    { id: 'luigi', nome: 'Luigi', classe: 'Caçador de Estrelas' },
    { id: 'peach', nome: 'Princesa Peach', classe: 'Líder do Reino' },
    { id: 'toad', nome: 'Toad', classe: 'Mensageiro Veloz' },
    { id: 'yoshi', nome: 'Yoshi', classe: 'Companheiro Saltador' },
    { id: 'bowser', nome: 'Bowser', classe: 'Boss Final' },
];

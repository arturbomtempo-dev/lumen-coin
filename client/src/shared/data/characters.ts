export type MarioCharacter =
    | 'mario'
    | 'luigi'
    | 'peach'
    | 'toad'
    | 'yoshi'
    | 'bowser'
    | 'institution';

export const MARIO_CHARACTERS: { id: MarioCharacter; name: string; title: string }[] = [
    { id: 'mario', name: 'Mario', title: 'Encanador Herói' },
    { id: 'luigi', name: 'Luigi', title: 'Caçador de Estrelas' },
    { id: 'peach', name: 'Princesa Peach', title: 'Líder do Reino' },
    { id: 'toad', name: 'Toad', title: 'Mensageiro Veloz' },
    { id: 'yoshi', name: 'Yoshi', title: 'Companheiro Saltador' },
    { id: 'bowser', name: 'Bowser', title: 'Boss Final' },
    { id: 'institution', name: 'Instituição', title: 'Entidade Educacional' },
];

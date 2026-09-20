# Jogo da Velha Arcade

Aplicativo Android nativo desenvolvido com **Kotlin** e **Jetpack Compose**, adaptando e empacotando o jogo de arcade completo com todas as mecânicas, efeitos visuais, áudios e lógica de inteligência artificial.

## Recursos Implementados

- **Modos de Jogo**:
  - Duelo contra IA em 4 níveis de dificuldade: Fácil, Médio, Difícil e Insano (Minimax invicto).
  - Duelo Local no mesmo aparelho (2 Jogadores) com modos Alternado e Blitz.
  - Modo Campanha: 10 fases estratégicas progressivas com missões e recompensas em estrelas.
  - Partidas rápidas e personalizáveis (escolha de quem inicia, tempo de turno, série melhor de 1 ou melhor de 3).

- **Economia & Progressão**:
  - Moedas, XP, Níveis de jogador, Troféus e sequência de vitórias (Streak).
  - Sistema de 10 vidas com recarga temporal automática.
  - Missões diárias dinâmicas com recompensas.
  - Conquistas e Passe da Temporada.

- **Loja de Personalização**:
  - 6 Conjuntos de Peças (Skins como Neon, Chama, Ouro, Cyber, etc.).
  - 6 Tabuleiros estilizados (Clássico, Oceano, Galáxia, etc.).
  - 6 Auras cosméticas vibrantes.

- **Áudio & Efeitos Sonoros**:
  - Trilha sonora do menu e música de partida com alternância inteligente.
  - Efeitos sonoros autênticos para início de partida ("Tá Pronto_ Já"), vitória ("Vitória no Show de Bola"), derrota ("A Máquina Levou Essa") e empate ("Deu Velha! Empate Técnico!").
  - Controles de volume independentes para SFX, música do menu e música do jogo.

- **Design & Arquitetura**:
  - Material Design 3 com paleta Dark Arcade vibrante (`#05091A`, ouro arcade, neon blue e coral).
  - Ícone adaptativo exclusivo Material You.
  - Totalmente funcional offline com assets locais (fontes, scripts e áudios).
  - Trata o botão voltar físico do Android para retornar entre telas e menus.

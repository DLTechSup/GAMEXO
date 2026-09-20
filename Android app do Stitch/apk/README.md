# Jogo da Velha Arcade — Guia para gerar o APK

Prompt e instruções para usar no **Google AI Studio** (ou Android Studio) e empacotar este jogo como aplicativo Android.

---

## 1. O que tem neste pacote

```
index.html                  → o jogo completo (interface + lógica)
support.js                  → runtime necessário, deve ficar ao lado do index.html
uploads/
  Menu-to-Game.mp3                          → música do menu (loop)
  musica para quando a partida começar.mp3  → música da partida (loop)
  Tá Pronto_ Já.mp3                         → efeito de largada
  Vitória no Show de Bola.mp3               → efeito de vitória
  A Máquina Levou Essa.mp3                  → efeito de derrota
  Deu Velha! Empate Técnico!.mp3            → efeito de empate
```

Os nomes dos arquivos de áudio **não podem ser alterados** — o jogo os carrega por nome, a partir da pasta `uploads/`.

---

## 2. Prompt para o Google AI Studio

Cole o texto abaixo no AI Studio junto com o ZIP:

> Tenho um jogo web completo e funcional (HTML + JavaScript, sem framework e sem etapa de build). Quero empacotá-lo como um aplicativo Android nativo usando WebView, sem alterar a interface nem a lógica do jogo.
>
> Gere um projeto Android Studio completo com:
> - `minSdk 24`, `targetSdk 34`, Kotlin
> - Uma única `MainActivity` com um `WebView` ocupando a tela inteira
> - Todos os arquivos do meu ZIP colocados em `app/src/main/assets/`, carregados com `file:///android_asset/index.html`
> - Configurações do WebView: `javaScriptEnabled = true`, `domStorageEnabled = true`, `mediaPlaybackRequiresUserGesture = false`, `allowFileAccess = true`
> - Orientação travada em retrato (`android:screenOrientation="portrait"`)
> - Tela cheia sem barra de título (tema `NoActionBar`), com as barras do sistema ocultas
> - Botão físico Voltar navegando dentro do WebView quando houver histórico, e saindo do app quando não houver
> - Nome do app: **Jogo da Velha Arcade**
> - `applicationId`: `com.arcade.jogodavelha`
>
> Não use permissão de internet obrigatória — o jogo roda offline. As fontes vêm do Google Fonts; se estiverem indisponíveis, o app deve continuar funcionando com as fontes do sistema.
>
> Ao final, gere as instruções para compilar o APK de release assinado.

---

## 3. Pontos críticos de configuração

**Áudio.** Sem `mediaPlaybackRequiresUserGesture = false` o Android bloqueia o autoplay e o jogo abre mudo. Essa linha é obrigatória:

```kotlin
webView.settings.mediaPlaybackRequiresUserGesture = false
```

**Progresso salvo.** O jogo guarda moedas, XP, fases, skins e configurações no armazenamento local do navegador. Sem `domStorageEnabled = true` nada é salvo entre sessões:

```kotlin
webView.settings.domStorageEnabled = true
```

**Assets.** A estrutura de pastas precisa ser preservada: `index.html` e `support.js` na raiz de `assets/`, e os `.mp3` dentro de `assets/uploads/`.

**Offline.** O jogo funciona sem internet. As fontes Google Fonts são o único recurso externo; a ausência delas não quebra nada.

---

## 4. O que o jogo já faz

**Telas:** Menu principal, Modos & Dificuldade, Mapa de Fases, Arena vs Bot, Duelo Local (2 jogadores), Lobby Wi-Fi/Bluetooth, Loja, Desafios, Perfil, Configurações, Pausa e os resultados de Vitória, Derrota e Empate.

**Jogabilidade:** IA em 4 níveis (Fácil, Médio, Difícil e Insano com minimax perfeito), timer por jogada configurável, sistema de dicas, 10 fases com desbloqueio progressivo e missões.

**Economia:** moedas, XP, níveis, troféus, sequência de vitórias, sistema de 10 vidas com recarga de 1 a cada 5 minutos, pacotes de moedas e recarga instantânea.

**Personalização:** 6 conjuntos de peças, 6 tabuleiros e 6 auras, todos compráveis e equipáveis, refletindo na partida.

**Progressão:** missões diárias, conquistas ligadas às estatísticas reais e Passe Ouro da temporada.

**Áudio:** música de menu e de partida com troca automática, efeitos nos momentos-chave, e três controles de volume independentes em Configurações.

---

## 5. Limitação conhecida

O **Lobby Wi-Fi/Bluetooth é simulado** — a interface e o fluxo estão completos, mas o pareamento real entre aparelhos exige código nativo Android (Wi-Fi Direct ou Bluetooth), que não pode ser feito dentro do WebView. Para tornar o multiplayer local funcional, esse módulo precisa ser implementado em Kotlin e conectado à tela por uma ponte JavaScript.

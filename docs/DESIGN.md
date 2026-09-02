# Sistema de Design - Guia de Estilo e Padrões de Interface

Este documento formaliza as especificações e diretrizes do sistema de design web corporativo. O objetivo é servir como fonte única de verdade para que desenvolvedores e agentes de IA desenvolvam novas páginas e módulos mantendo total fidelidade e consistência visual.

***

## Overview

### Personalidade Visual

O sistema adota uma identidade visual **corporativa moderna**, equilibrando elegância, densidade informacional e alta usabilidade, com um toque de calor editorial que confere acolhimento e sofisticação à interface:

* **Profissional e Confiável**: Predomínio de tons neutros quentes com acentos em Azul Royal vibrante (`#2f80ed`) e detalhe institucional em Verde Floresta (`#337259`).
* **Estruturado e Quase Plano (_Near-Flat_)**: A hierarquia e a separação espacial são obtidas prioritariamente por bordas sutis e contraste de superfícies. Sombras são utilizadas com moderação para leve desacoplamento de camadas e para o efeito tátil de botões escuros (_inset shadow_).
* **Densidade Equilibrada**: O espaçamento é otimizado para sistemas de produtividade e painéis operacionais, com respiro editorial entre seções (80px–208px), permitindo rápida visualização de métricas e navegação fluida sem poluição visual.
* **Foco em Legibilidade e Acessibilidade**: Alto contraste em todos os textos, áreas de clique confortáveis (mínimo 40px para ações de toque/clique) e estados de foco nítidos com sombra suave difusa.

### Arquitetura de Layout (Shell da Aplicação)

* **Estrutura de Viewport Fixa**: A aplicação ocupa `100vw` e `100vh` com `overflow: hidden` no `body`.
* **Cabeçalho Superior Fixo (_App Header_)**: Altura de `70px`, `z-index: 100`, com borda inferior de destaque de `3px solid #337259`.
* **Barra Lateral Fixa (_App Sidebar_)**: Largura de `250px`, rolagem vertical independente (`overflow-y: auto`), fixada à esquerda.
* **Área de Conteúdo (_App Content_)**: Ocupa o restante do espaço (`flex: 1`), com rolagem vertical própria (`overflow-y: auto`) e preenchimento interno padronizado de `1.5rem 2rem` (24px 32px). Largura máxima de conteúdo interno: `1200px` quando aplicável.

***

## Colors

### Variáveis CSS Nativas (`:root`)

```css
:root {
  --font-family: "Montserrat", sans-serif;
  --primary-color: #2f80ed;
  --primary-hover: #1f6cd2;
  --primary-active: #0f55b2;
  --primary-text: #ffffff;
  --surface-ground: #f7f4ed;
  --surface-card: #ffffff;
  --surface-border: #e2e8f0;
  --surface-border-warm: #eceae4;
  --surface-hover: #f1f5f9;
  --text-color: #1e293b;
  --text-secondary: #64748b;
  --header-height: 70px;
  --sidebar-width: 250px;
  --border-radius: 6px;
}
```

### Paleta Principal e Ações

* **Primária / Ação Principal** (`#2f80ed`): Utilizada em botões principais, links ativos, ícones de identidade e estados de foco.
  * _Hover_: `#1f6cd2`
  * _Active / Pressionado_: `#0f55b2`
  * _Texto em Contraste_: `#ffffff`
  * _Fundo Ativo Translúcido_: `rgba(47, 128, 237, 0.08)` (itens de menu selecionados)
  * _Anel de Foco (Focus Ring)_: `rgba(47, 128, 237, 0.15)`
* **Acento Institucional** (`#337259`): Verde corporativo aplicado como friso decorativo na borda inferior do cabeçalho principal (`border-bottom: 3px solid #337259`).
* **Gradiente da Marca**: `linear-gradient(135deg, #2f80ed 0%, #1d4ed8 100%)` aplicado no contêiner do ícone do logotipo.

### Superfícies e Neutros

* **Fundo da Aplicação (`--surface-ground`)** (`#f7f4ed`): Tom cream quente para o fundo geral e áreas reservadas. Substitui o cinza frio por um tom acolhedor que reduz a fadiga visual.
* **Superfície de Cards e Painéis (`--surface-card`)** (`#ffffff`): Branco puro para cartões de métricas, tabelas, formulários e barra lateral.
* **Borda Padrão (`--surface-border`)** (`#e2e8f0`): Cinza claro suave (Slate 200) para divisores, bordas de cards e inputs.
* **Borda Quente (`--surface-border-warm`)** (`#eceae4`): Tom cálido para bordas de cartões e divisores quando se deseja um acento mais orgânico. Alternativa ao `#e2e8f0` para seções com tom editorial.
* **Superfície em Hover (`--surface-hover`)** (`#f1f5f9`): Cinza azulado sutil (Slate 100) para hover em botões de ícone e links da navegação.
* **Fundo de Campos de Entrada** (`#f7f4ed`): Fundo cream para inputs em estado padrão, alinhado ao tom da página.

### Escala de Opacidade (Tons Neutros)

Para criar coesão tonal, tons de cinza são derivados de `#1c1c1c` em níveis variados de opacidade:

| Nome              | Valor                    | Uso                                              |
| :---------------- | :----------------------- | :----------------------------------------------- |
| **Charcoal 100%** | `#1c1c1c`                | Texto principal alternativo, superfícies escuras |
| **Charcoal 83%**  | `rgba(28, 28, 28, 0.83)` | Texto secundário forte                           |
| **Charcoal 82%**  | `rgba(28, 28, 28, 0.82)` | Corpo de texto                                   |
| **Charcoal 40%**  | `rgba(28, 28, 28, 0.40)` | Bordas interativas, contorno de botões ghost     |
| **Charcoal 4%**   | `rgba(28, 28, 28, 0.04)` | Fundo hover sutil, micro-tintas                  |
| **Charcoal 3%**   | `rgba(28, 28, 28, 0.03)` | Overlays de profundidade, camadas sutis          |

### Tipografia

* **Texto Principal (`--text-color`)** (`#1e293b`): Slate 800 para títulos, valores numéricos e textos de leitura principal.
* **Texto Secundário (`--text-secondary`)** (`#64748b`): Slate 500 para subtítulos, rótulos de métricas, títulos de seções e ícones neutros.

### Paleta Semântica para Indicadores e Métricas

Cada indicador utiliza um par de cores: fundo pastel e ícone/texto de alto contraste:

* **Informativo / Azul**:
  * Fundo do Ícone: `#e0f2fe`
  * Ícone / Destaque: `#0284c7`
* **Sucesso / Verde**:
  * Fundo do Ícone: `#dcfce7`
  * Ícone / Destaque: `#16a34a`
* **Alerta / Âmbar**:
  * Fundo do Ícone: `#fef3c7`
  * Ícone / Destaque: `#d97706`
* **Métrica / Roxo**:
  * Fundo do Ícone: `#f3e8ff`
  * Ícone / Destaque: `#9333ea`

***

## Typography

### Família Tipográfica

* **Fonte Principal (Headlines, Body e Labels)**: `"Montserrat", sans-serif` (Google Fonts).
* **Pesos Utilizados**:
  * `400` (Regular): Textos descritivos, placeholders e corpo de texto.
  * `500` (Medium): Links da barra lateral e rótulos de métricas.
  * `600` (SemiBold): Botões principais, links de navegação ativos e títulos de seção.
  * `700` (Bold): **Reservado exclusivamente para** títulos principais (H1), títulos de cartões, valores de métricas (KPI) e seções de menu. Não utilizar em corpo de texto ou botões.

### Escala Tipográfica e Hierarquia

| Elemento / Aplicação           | Tamanho                       | Peso                    | Cor                                | Letter-Spacing / Estilo                           |
| :----------------------------- | :---------------------------- | :---------------------- | :--------------------------------- | :------------------------------------------------ |
| **Display Hero (se houver)**   | `3.75rem` (60px)              | `600` (`font-semibold`) | `#1e293b`                          | `-1.5px`, line-height `1.00`                      |
| **Título de Seção**            | `2.25rem` (36px)              | `600` (`font-semibold`) | `#1e293b`                          | `-0.9px`, line-height `1.10`                      |
| **Título da Página (H1)**      | `1.5rem` (24px / `text-2xl`)  | `700` (`font-bold`)     | `#1e293b`                          | Padrão, line-height `1.25`                        |
| **Valor de Métrica (KPI)**     | `1.5rem` (24px)               | `700` (`font-bold`)     | `#1e293b`                          | Linha de altura compacta (`1.15`)                 |
| **Título de Card**             | `1.25rem` (20px)              | `700` (`font-bold`)     | `#1e293b`                          | Margem inferior `0.5rem`                          |
| **Nome da Marca / Logo**       | `1.15rem` (18.4px)            | `700` (`font-bold`)     | `#1e293b`                          | Alinhamento vertical central                      |
| **Links de Navegação**         | `0.9rem` (14.4px)             | `500` / `600`           | `#1e293b` / `#2f80ed`              | `transition: all 0.2s ease`                       |
| **Campos de Texto / Busca**    | `0.9rem` (14.4px)             | `400`                   | `#1e293b`                          | Placeholder `#64748b`                             |
| **Subtítulos e Apoios**        | `0.875rem` (14px / `text-sm`) | `400` / `500`           | `#64748b` (`text-color-secondary`) | `margin-bottom: 1rem`                             |
| **Rótulo de Métrica (Card)**   | `0.85rem` (13.6px)            | `500` (`font-medium`)   | `#64748b`                          | Caixa normal                                      |
| **Rótulos de Seção (Sidebar)** | `0.75rem` (12px)              | `700` (`font-bold`)     | `#64748b`                          | Caixa alta (`uppercase`), `letter-spacing: 0.5px` |
| **Corpo de Texto**             | `1rem` (16px)                 | `400`                   | `#1e293b`                          | `line-height: 1.5`                                |

### Princípios Tipográficos

* **Compressão em displays**: Headlines grandes (36px–60px) utilizam letter-spacing negativo (`-0.9px` a `-1.5px`) para impacto editorial. Corpo de texto e UI mantêm tracking normal para leitura confortável.
* **Dois pesos dominantes, papéis claros**: `400` para corpo/UI/inputs e `600` para headings/seções. O peso `700` é reservado para elementos de máximo destaque (H1, KPIs).
* **Line-height ajustado à escala**: Quanto maior o tamanho, mais compacta a entrelinha. Display usa `1.00`–`1.10`, corpo usa `1.5`.

***

## PrimeFlex Layout & Utilitários (PrimeFlex Design System)

A biblioteca **PrimeFlex (v3.3.1)** é a base utilitária de classes adotada para o posicionamento, grid responsivo, alinhamento flexível, espaçamento e visibilidade dinâmica.

### 1. Sistema de Grid Responsivo (12 Colunas)

* **Contêiner**: `.grid` (adiciona espaçamento gutter nativo e compensação de margens negativas).
* **Colunas Responsivas**:
  * `col-12`: Ocupa 100% da largura em dispositivos móveis (< 768px).
  * `md:col-6`: Ocupa 50% (2 colunas por linha) em tablets (≥ 768px).
  * `lg:col-3`: Ocupa 25% (4 colunas por linha) em desktops (≥ 992px).
  * `xl:col-*`: Ajustes para telas ultra-largas (≥ 1200px).

```html
<!-- Padrão de Grid para Métricas e Indicadores -->
<div class="grid mb-4">
  <div class="col-12 md:col-6 lg:col-3">
    <!-- Metric Card 1 -->
  </div>
  <div class="col-12 md:col-6 lg:col-3">
    <!-- Metric Card 2 -->
  </div>
  <div class="col-12 md:col-6 lg:col-3">
    <!-- Metric Card 3 -->
  </div>
  <div class="col-12 md:col-6 lg:col-3">
    <!-- Metric Card 4 -->
  </div>
</div>
```

### 2. Utilitários Flexbox e Alinhamento

* **Contêineres Flex**: `.flex`, `.inline-flex`, `.flex-column`.
* **Alinhamento Vertical (Cross-Axis)**:
  * `align-items-center`: Centraliza verticalmente itens em barras e cabeçalhos.
* **Distribuição Horizontal (Main-Axis)**:
  * `justify-content-between`: Distribui extremidades (ex.: título da página à esquerda, botão de ação à direita).
  * `justify-content-center`: Centraliza elementos interna e horizontalmente.
* **Espaçamento entre Itens (_Gap_)**:
  * `gap-1`: 0.25rem (4px)
  * `gap-2`: 0.5rem (8px) – _Padrão em barras de navegação e agrupamentos de botões_
  * `gap-3`: 1rem (16px) – _Padrão entre blocos maiores_

### 3. Espaçamento (Padding e Margins)

* **Margens Verticais de Ritmo**:
  * `mb-4`: Margem inferior de `1.5rem` (24px) entre o cabeçalho da página, o grid de métricas e os cartões de conteúdo.
  * `mb-2`: Margem inferior de `0.5rem` (8px) para ícones de áreas reservadas.
  * `mt-3`: Margem superior de `1rem` (16px) para separadores de seções na barra lateral.
  * `mt-1`: Margem superior de `0.25rem` (4px) para subtítulos sob títulos H1.
  * `ml-2`: Margem esquerda de `0.5rem` (8px) para espaçar o bloco do perfil no cabeçalho.
* **Preenchimento (_Padding_)**:
  * `p-4`: Preenchimento interno de `1.5rem` (24px) utilizado em áreas reservadas e contêineres vazios.
  * Para espaçamento editorial entre grandes seções, utilizar margens manuais de `80px`–`208px` conforme a hierarquia da página.

### 4. Tipografia e Cores Utilitárias do PrimeFlex

* **Tamanhos de Fonte**:
  * `text-sm`: 0.875rem (14px) – subtítulos, legendas e textos de perfil.
  * `text-2xl`: 1.5rem (24px) – títulos de página (H1).
  * `text-4xl`: 2.5rem (40px) – ícones de destaque e estados vazios.
* **Pesos de Fonte**:
  * `font-medium`: Peso 500.
  * `font-semibold`: Peso 600.
  * `font-bold`: Peso 700.
* **Cores Utilitárias Integradas**:
  * `text-color-secondary`: `#64748b` (texto de apoio e ícones inativos).
  * `text-white`: `#ffffff` (texto em fundos primários).
  * `bg-primary`: `#2f80ed` (fundo com a cor primária).

### 5. Bordas, Formas e Superfícies Utilitárias

* **Formas e Arredondamento**:
  * `border-circle`: Aplica `border-radius: 50%` (utilizado no avatar do usuário e ícones circulares).
  * `border-round`: Aplica o raio padrão de borda (`--border-radius: 6px`).
* **Bordas Decorativas**:
  * `border-2`: Espessura de borda de 2px.
  * `border-dashed`: Borda pontilhada/tracejada para caixas de upload ou estados vazios.
* **Superfícies**:
  * `surface-ground`: Aplica a cor de fundo padrão (`#f7f4ed`).

### 6. Visibilidade e Responsividade Condicional

* **Esconder/Exibir por Dispositivo**:
  * `hidden md:block`: Oculta o elemento em celulares (< 768px) e exibe como bloco em telas médias e superiores (utilizado no campo de busca do cabeçalho).
  * `hidden lg:inline`: Oculta o elemento até telas grandes (≥ 992px) e exibe inline (utilizado no nome do usuário junto ao avatar).
* **Interação**:
  * `cursor-pointer`: Define o cursor do mouse como ponteiro de clique (utilizado no agrupamento de perfil).

***

## Elevation

### Tratamento de Profundidade

A aplicação adota uma abordagem **quase plana (_near-flat_)**, onde a profundidade é comunicada através de bordas, sombras sutis e um sistema de níveis bem definido:

### Níveis de Profundidade

| Nível                        | Tratamento                                                                                                                 | Uso                                                                |
| :--------------------------- | :------------------------------------------------------------------------------------------------------------------------- | :----------------------------------------------------------------- |
| **Flat (Level 0)**           | Sem sombra, fundo `#f7f4ed`                                                                                                | Superfície da página, conteúdo geral                               |
| **Bordered (Level 1)**       | `1px solid #e2e8f0` (padrão) ou `1px solid #eceae4` (quente)                                                               | Cartões, imagens, divisores                                        |
| **Elevated (Level 2)**       | `box-shadow: 0 1px 2px rgba(0,0,0,0.04)`                                                                                   | Cartões de conteúdo com leve desacoplamento                        |
| **Header (Level 3)**         | `box-shadow: 0 1px 3px rgba(0,0,0,0.05)`                                                                                   | Cabeçalho fixo da aplicação                                        |
| **Inset (Level 4)**          | `rgba(255,255,255,0.2) 0px 0.5px 0px 0px inset, rgba(0,0,0,0.2) 0px 0px 0px 0.5px inset, rgba(0,0,0,0.05) 0px 1px 2px 0px` | Botões escuros com efeito tátil "pressionado na superfície"        |
| **Focus (Level 5)**          | `box-shadow: 0 0 0 3px rgba(47, 128, 237, 0.15)`                                                                           | Estado de foco em elementos interativos                            |
| **Focus Warm (Alternativo)** | `box-shadow: rgba(0,0,0,0.1) 0px 4px 12px`                                                                                 | Foco suave e difuso para botões e cards (alternativa ao ring azul) |

### Sombras Específicas

* **Cabeçalho (_Header_)**: `box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);`
* **Cartões (_Cards_)**: `box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);`
* **Estado de Foco Padrão**: `box-shadow: 0 0 0 3px rgba(47, 128, 237, 0.15);`
* **Botão Dark (Inset Shadow)**: `rgba(255, 255, 255, 0.2) 0px 0.5px 0px 0px inset, rgba(0, 0, 0, 0.2) 0px 0px 0px 0.5px inset, rgba(0, 0, 0, 0.05) 0px 1px 2px 0px`
  * A linha branca superior (`inset 0px 0.5px`) cria um destaque sutil no topo do botão.
  * O anel escuro interno (`inset 0px 0px 0px 0.5px`) define a borda.
  * A sombra inferior (`0px 1px 2px`) completa o efeito tátil de profundidade.

### Filosofia de Profundidade

* **Bordas como mecanismo primário de contenção**: Cartões e painéis são delimitados por bordas (`#e2e8f0` ou `#eceae4`), não por sombras pesadas.
* **Sombras apenas para desacoplamento**: Usadas com moderação e opacidade extremamente baixa (0.04–0.05).
* **Inset shadow como assinatura visual**: O efeito de botão dark com múltiplas camadas de sombra interna é o detalhe distintivo para ações primárias escuras.

### Sistema de Arredondamento (Border Radius)

| Escala          | Valor    | Aplicação                                                         |
| :-------------- | :------- | :---------------------------------------------------------------- |
| **Micro**       | `4px`    | Botões pequenos, elementos interativos compactos                  |
| **Padrão**      | `6px`    | Botões principais, inputs, cartões de métricas, menu de navegação |
| **Ícone**       | `8px`    | Contêineres de ícones destacados (48x48px, 38x38px), logotipo     |
| **Card**        | `12px`   | Cartões de conteúdo geral, contêineres de imagem, galerias        |
| **Container**   | `16px`   | Contêineres amplos, seções de rodapé                              |
| **Pill Search** | `20px`   | Campo de busca em formato de cápsula                              |
| **Circle**      | `50%`    | Avatar do usuário, ações de ícone (`border-circle` no PrimeFlex)  |
| **Full Pill**   | `9999px` | Botões de ação em pílula, toggles, ícones de ação compactos       |

***

## Components

### 1. Barra Superior (`.app-header`)

* **Dimensões**: `height: 70px; width: 100vw; padding: 0 1.25rem;`.
* **Estilização**: Fundo `#ffffff`, borda inferior `3px solid #337259`, sombra leve `0 1px 3px rgba(0,0,0,0.05)`.
* **Estrutura HTML com Classes PrimeFlex**:

```html
<header class="app-header">
  <!-- Lado Esquerdo: Navegação & Identidade -->
  <div class="flex align-items-center gap-2">
    <button class="btn-icon" title="Menu Principal">
      <i class="pi pi-bars"></i>
    </button>
    <div class="header-brand">
      <div class="logo-icon">
        <i class="pi pi-compass"></i>
      </div>
      <span>Nome do Sistema</span>
    </div>
  </div>

  <!-- Centro: Campo de Busca Responsivo -->
  <div class="search-container hidden md:block">
    <i class="pi pi-search search-icon"></i>
    <input
      type="search"
      class="search-input"
      placeholder="Pesquisar registros, módulos ou ações... (Ctrl+K)"
    />
  </div>

  <!-- Lado Direito: Ações & Perfil -->
  <div class="flex align-items-center gap-2">
    <button class="btn-icon" title="Assistente Virtual">
      <i class="pi pi-sparkles"></i>
    </button>
    <button class="btn-icon" title="Mensagens">
      <i class="pi pi-envelope"></i>
    </button>
    <button class="btn-icon" title="Notificações">
      <i class="pi pi-bell"></i>
    </button>

    <!-- Avatar do Usuário -->
    <div class="flex align-items-center gap-2 ml-2 cursor-pointer">
      <div
        class="w-2rem h-2rem border-circle flex align-items-center justify-content-center bg-primary text-white font-semibold text-sm"
      >
        US
      </div>
      <span class="text-sm font-medium hidden lg:inline">Usuário</span>
    </div>
  </div>
</header>
```

### 2. Barra Lateral de Navegação (`.app-sidebar`)

* **Dimensões**: `width: 250px; padding: 1rem 0; overflow-y: auto;`.
* **Divisores de Seção (`.nav-section-title`)**: Texto em `0.75rem`, `font-weight: 700`, `text-transform: uppercase`, cor `#64748b`, `padding: 0.5rem 1.25rem`. Utilizar `.mt-3` para seções subsequentes.
* **Links de Navegação (`.nav-link`)**:
  * Layout: `display: flex; align-items: center; gap: 0.75rem; padding: 0.75rem 1.25rem;`.
  * Tipografia: `font-size: 0.9rem; font-weight: 500; color: #1e293b;`.
  * Borda Esquerda: `3px solid transparent`.
  * **Hover**: Fundo `#f1f5f9`, cor `#2f80ed`.
  * **Ativo (`.active`)**: Fundo `rgba(47, 128, 237, 0.08)`, cor `#2f80ed`, peso `600`, borda esquerda `3px solid #2f80ed`.

```html
<aside class="app-sidebar">
  <span class="nav-section-title">Principal</span>
  <a href="#dashboard" class="nav-link active">
    <i class="pi pi-home"></i>
    <span>Dashboard</span>
  </a>
  <a href="#registros" class="nav-link">
    <i class="pi pi-folder"></i>
    <span>Meus Registros</span>
  </a>

  <span class="nav-section-title mt-3">Ferramentas</span>
  <a href="#configuracoes" class="nav-link">
    <i class="pi pi-sliders-h"></i>
    <span>Configurações</span>
  </a>
</aside>
```

### 3. Cabeçalho de Conteúdo da Página (_Page Header_)

* **Estrutura com PrimeFlex**:

```html
<div class="flex justify-content-between align-items-center mb-4">
  <div>
    <h1 class="text-2xl font-bold">Painel Geral</h1>
    <p class="text-sm text-color-secondary mt-1">
      Visão integrada das operações e indicadores.
    </p>
  </div>
  <button class="btn-primary">
    <i class="pi pi-plus"></i>
    <span>Novo Registro</span>
  </button>
</div>
```

### 4. Cartão de Métrica / Indicador KPI (`.metric-card`)

* **Estrutura**: Fundo `#ffffff`, borda `1px solid #e2e8f0`, raio `6px`, padding `1.25rem`, display flex com alinhamento central e `gap: 1rem`.
* **Estrutura HTML com PrimeFlex**:

```html
<div class="metric-card">
  <!-- Ícone com fundo pastel e cor temática -->
  <div class="metric-icon" style="background: #e0f2fe; color: #0284c7;">
    <i class="pi pi-file"></i>
  </div>
  <div class="metric-info">
    <h4>Total Processado</h4>
    <span>1.224</span>
  </div>
</div>
```

### 5. Cartão de Conteúdo Geral (`.card`)

* **Estrutura Padrão**: Fundo `#ffffff`, borda `1px solid #e2e8f0`, raio `12px`, padding `1.5rem`, margem inferior `1.5rem`, sombra `0 1px 2px rgba(0,0,0,0.04)`.

```html
<div class="card">
  <div class="card-title">Área de Trabalho</div>
  <div class="card-subtitle">
    Insira aqui tabelas, formulários ou gráficos do seu módulo.
  </div>

  <!-- Área de Conteúdo ou Tabela -->
</div>
```

* **Variante Quente (Estilo Editorial)**: Fundo `#f7f4ed`, borda `1px solid #eceae4`, raio `12px`, sem sombra. Ideal para seções com tom mais acolhedor.

```html
<div class="card" style="background: #f7f4ed; border: 1px solid #eceae4; border-radius: 12px; padding: 1.5rem;">
  <div class="card-title">Seção Editorial</div>
  <div class="card-subtitle">Conteúdo com tom mais quente e orgânico.</div>
</div>
```

### 6. Botões

* **Botão de Ação Primária (`.btn-primary`)**:
  * Fundo `#2f80ed`, texto `#ffffff`, sem borda, padding `0.65rem 1.25rem`, raio `6px`, peso `600`, cursor pointer, `gap: 0.5rem`.
  * Hover: Fundo `#1f6cd2`.
  * Active: Fundo `#0f55b2` (opacity 0.8 como alternativa).
* **Botão Dark com Inset Shadow (`.btn-primary-dark`)**:
  * Fundo `#1c1c1c`, texto `#fcfbf8`, padding `0.5rem 1rem` (8px 16px), raio `6px`, peso `600`.
  * Sombra: `rgba(255,255,255,0.2) 0px 0.5px 0px 0px inset, rgba(0,0,0,0.2) 0px 0px 0px 0.5px inset, rgba(0,0,0,0.05) 0px 1px 2px 0px`.
  * Active: opacity `0.8`.
  * Focus: `rgba(0,0,0,0.1) 0px 4px 12px`.
  * Uso: CTA principal quando se deseja contraste máximo com o fundo cream.
* **Botão Ghost / Outline (`.btn-ghost`)**:
  * Fundo transparente, texto `#1c1c1c`, padding `0.5rem 1rem`, raio `6px`.
  * Borda: `1px solid rgba(28, 28, 28, 0.4)`.
  * Active: opacity `0.8`.
  * Focus: `rgba(0,0,0,0.1) 0px 4px 12px`.
  * Uso: Ações secundárias.
* **Botão Cream Surface (`.btn-cream`)**:
  * Fundo `#f7f4ed`, texto `#1c1c1c`, padding `0.5rem 1rem`, raio `6px`, sem borda.
  * Active: opacity `0.8`.
  * Uso: Ações terciárias, botões de barra de ferramentas.
* **Botão Pill / Ícone Compacto (`.btn-pill`)**:
  * Fundo `#f7f4ed`, texto `#1c1c1c`, raio `9999px`.
  * Sombra: mesmo padrão inset do dark button.
  * Opacidade: `0.5` (padrão), `0.8` (ativo).
  * Uso: Toggles, ações complementares, modo de plano.
* **Botão de Ícone (`.btn-icon`)**:
  * Fundo transparente, sem borda, `width: 40px; height: 40px; border-radius: 50%;` cor `#64748b`.
  * Hover: Fundo `#f1f5f9`, cor `#1e293b`.

### 7. Campos de Entrada (Inputs)

* **Padrão**: Fundo `#f7f4ed`, texto `#1e293b`, borda `1px solid #e2e8f0`, raio `6px`, padding `0.5rem 0.75rem`.
* **Placeholder**: Cor `#64748b`, peso `400`.
* **Foco**: `box-shadow: 0 0 0 3px rgba(47, 128, 237, 0.15)` (ring azul padrão do sistema).

### 8. Estado Vazio e Área Reservada (_Empty State / Placeholder_)

* **Estrutura com Classes PrimeFlex**:

```html
<div class="p-4 border-dashed border-2 border-round surface-ground text-center">
  <i class="pi pi-inbox text-4xl text-color-secondary mb-2"></i>
  <p class="text-color-secondary">
    Área reservada para o conteúdo dinâmico da página.
  </p>
</div>
```

### 9. Barra de Estatísticas (_Stats Bar_)

* **Estrutura**: Layout horizontal com `gap: 2rem` (32px), distribuído uniformemente.
* **Valores**: Tamanho `2.25rem` (36px) ou `3rem` (48px), peso `600`, cor `#1e293b`, letter-spacing `-1.2px`, line-height `1.10`.
* **Rótulos**: Tamanho `1rem` (16px), peso `400`, cor `#64748b`.
* **Em mobile**: Empilhar verticalmente.

```html
<div class="flex justify-content-center gap-4 flex-wrap">
  <div class="text-center">
    <div class="text-4xl font-semibold" style="letter-spacing: -1.2px;">1.224</div>
    <div class="text-sm text-color-secondary mt-1">Registros Processados</div>
  </div>
  <div class="text-center">
    <div class="text-4xl font-semibold" style="letter-spacing: -1.2px;">98.5%</div>
    <div class="text-sm text-color-secondary mt-1">Taxa de Sucesso</div>
  </div>
  <div class="text-center">
    <div class="text-4xl font-semibold" style="letter-spacing: -1.2px;">342</div>
    <div class="text-sm text-color-secondary mt-1">Usuários Ativos</div>
  </div>
</div>
```

***

## Layout & Espaçamento

### Shell da Aplicação (Fixo)

A estrutura principal da aplicação segue o modelo de shell fixo com três áreas:

```
+----------------------------------------------------------+
|  .app-header (70px, z-index: 100)                        |
+----------------------------------------------------------+
| .app-sidebar  |  .app-content (flex: 1, overflow-y: auto)|
| (250px)       |                                          |
| overflow-y:   |  padding: 1.5rem 2rem                    |
| auto          |  max-width conteúdo: 1200px              |
|               |                                          |
+----------------------------------------------------------+
```

### Filosofia de Espaçamento

* **Editorial entre seções**: Grandes blocos de conteúdo são separados por margens generosas de `80px` a `208px`, criando um ritmo de leitura que alterna entre foco concentrado e descanso visual. O fundo cream (`#f7f4ed`) faz esses espaços parecerem acolhedores, não vazios.
* **Denso dentro dos componentes**: Espaçamento interno de cartões e elementos de UI permanece compacto (`12px`–`24px`), otimizado para produtividade e escaneamento rápido de informações.
* **Escala de espaçamento**: `8px, 10px, 12px, 16px, 24px, 32px, 40px, 56px, 80px, 96px, 128px, 176px, 192px, 208px`.

### Largura Máxima de Conteúdo

Quando o conteúdo interno da área `.app-content` se beneficia de uma largura controlada (ex.: formulários, textos longos, dashboards), aplicar `max-width: 1200px` com centralização. Para tabelas de dados e conteúdos que exigem largura total, utilizar `100%`.

***

## Responsivo

### Breakpoints de Referência

| Nome              | Largura      | Comportamento                                      |
| :---------------- | :----------- | :------------------------------------------------- |
| **Mobile Small**  | < 600px      | Coluna única, padding reduzido                     |
| **Mobile**        | 600px–768px  | Coluna única padrão                                |
| **Tablet**        | 768px–992px  | Grid 2 colunas (`md:col-6`), sidebar pode colapsar |
| **Desktop**       | 992px–1200px | Grid 4 colunas (`lg:col-3`), layout completo       |
| **Large Desktop** | ≥ 1200px     | Grid expandido (`xl:col-*`), margens generosas     |

### Estratégia de Colapso (Collapsing Strategy)

* **Hero / Headlines**: Escala progressiva — `60px → 48px → 36px` com letter-spacing proporcional.
* **Navegação**: Links horizontais → menu hamburger abaixo de `768px`. Campo de busca oculto em mobile (`hidden md:block`).
* **Cards de métricas**: 4 colunas (desktop) → 2 colunas (tablet) → 1 coluna (mobile).
* **Grid de conteúdo**: 3 colunas → 2 colunas → empilhado vertical.
* **Stats bar**: Horizontal com gap generoso → empilhado vertical.
* **Sidebar**: Fixa em desktop → colapsável/overlay em mobile.
* **Footer**: Multi-coluna → coluna única empilhada.
* **Espaçamento entre seções**: `128px+` em desktop → `64px` em mobile.

### Touch Targets

* Botões: padding mínimo `8px 16px` (toque confortável).
* Navegação: espaçamento adequado entre itens.
* Pill buttons: raio `9999px` cria alvos de toque amplos e amigáveis.
* Ícones de ação: dimensão mínima `40x40px`.

***

## Do's and Don'ts

### O que Fazer (Do's)

* **Usar as Classes Utilitárias do PrimeFlex**: Para flexbox, alinhamento, gaps e grid responsivo, utilize sempre classes PrimeFlex (`flex`, `align-items-center`, `justify-content-between`, `gap-2`, `col-12 md:col-6 lg:col-3`) em vez de recriar regras CSS isoladas.
* **Manter a Viewport Travada**: O contêiner pai `.app-shell` deve preservar altura de `100vh` e rolagem independente na área `.app-content` e na barra lateral.
* **Preservar o Friso Institucional**: Sempre incluir a borda `border-bottom: 3px solid #337259` no cabeçalho superior.
* **Usar os Pares de Cores em Métricas**: Combine sempre o fundo pastel suave com o ícone em tom mais escuro e saturado (ex: fundo `#dcfce7` com ícone `#16a34a`).
* **Aplicar o Fundo Cream (`#f7f4ed`)**: Usar como `--surface-ground` para o fundo da aplicação. É a assinatura de calor do sistema — nunca substituir por branco puro (`#ffffff`) no fundo da página.
* **Derivar Cinzas por Opacidade**: Sempre que possível, derive tons de cinza a partir de `#1c1c1c` em níveis de opacidade (`0.03`, `0.04`, `0.40`, `0.82`) para manter coesão tonal.
* **Respeitar os Raios de Curvatura**: Utilize a escala completa — `4px` (micro), `6px` (padrão), `8px` (ícones), `12px` (cards), `16px` (containers), `20px` (pill search), `50%` (circle), `9999px` (full pill).
* **Sinalizar Navegação Ativa**: Todo link ativo na barra lateral deve exibir o fundo azul claro `rgba(47, 128, 237, 0.08)` e o traço à esquerda de `3px solid #2f80ed`.
* **Manter Responsividade no Campo de Busca**: Oculte o campo de busca global em mobile usando `hidden md:block` e mostre o menu de opções compacto.
* **Usar Inset Shadow em Botões Dark**: O efeito multicamada (`rgba(255,255,255,0.2)` inset + `rgba(0,0,0,0.2)` inset + drop shadow) é a assinatura visual dos botões escuros — não o substitua por sombras simples.
* **Usar Bordas para Contenção**: Prefira `1px solid #e2e8f0` ou `1px solid #eceae4` para delimitar cartões. Evite sombras pesadas como mecanismo primário de separação.
* **Aplicar Letter-Spacing Negativo em Headlines**: Para títulos de `36px` ou maiores, usar `-0.9px` a `-1.5px` de letter-spacing com line-height compacta (`1.00`–`1.10`).
* **Peso 700 com moderação**: Reservar `font-weight: 700` exclusivamente para H1, títulos de cartões e valores de KPI. Demais elementos usam `400`, `500` ou `600`.

### O que Não Fazer (Don'ts)

* **Não Utilizar Posicionamentos Flutuantes (`float`)**: Toda a diagramação deve ser conduzida através de `grid` e `flex` do PrimeFlex.
* **Não Utilizar Sombras Pesadas**: Evite sombras escuras ou difusas (como `0 10px 25px rgba(0,0,0,0.2)`). O design é quase plano e sustentado por bordas finas e sombras de baixíssima opacidade.
* **Não Usar Branco Puro como Fundo da Página**: O fundo da aplicação é `#f7f4ed` (cream). Branco (`#ffffff`) é reservado para cards e superfícies elevadas.
* **Não Misturar Famílias de Fontes**: Não utilize fontes serifadas ou fontes alternativas fora da família **Montserrat**.
* **Não Alterar a Altura do Header**: O cabeçalho deve manter estritamente os `70px` definidos na variável `--header-height`.
* **Não Usar Cores Primárias Arbitrárias**: Ações primárias devem sempre utilizar a escala `#2f80ed` (normal), `#1f6cd2` (hover) e `#0f55b2` (active).
* **Não Remover o Anel de Foco**: Nunca defina `outline: none` sem substituir por uma indicação visual clara de foco (`box-shadow: 0 0 0 3px rgba(47, 128, 237, 0.15)`).
* **Não Aplicar** **`border-radius: 9999px`** **em Botões Retangulares**: O raio de pílula completa é reservado para ações de ícone, toggles e botões compactos. Botões de ação com texto usam `6px`.
* **Não Usar** **`font-weight: 700`** **em Corpo de Texto ou Botões**: O peso 700 é exclusivo para elementos de máximo destaque (H1, KPIs, títulos de cards).
* **Não Introduzir Cores Saturadas Arbitrárias**: A paleta é intencionalmente contida. Cores vibrantes só entram via paleta semântica (métricas) ou azul primário (`#2f80ed`).
* **Não Misturar Estilos de Borda**: Use `#e2e8f0` para bordas utilitárias padrão, `#eceae4` para bordas com tom editorial quente, e `rgba(28,28,28,0.4)` para bordas interativas (ex.: botão ghost).

***

## Referência Rápida para Agentes

### Cores Essenciais

| Função               | Valor                             |
| :------------------- | :-------------------------------- |
| Fundo da Página      | `#f7f4ed`                         |
| Fundo de Cards       | `#ffffff`                         |
| Texto Principal      | `#1e293b`                         |
| Texto Secundário     | `#64748b`                         |
| Ação Primária        | `#2f80ed`                         |
| Hover Primário       | `#1f6cd2`                         |
| Active Primário      | `#0f55b2`                         |
| Acento Institucional | `#337259`                         |
| Borda Padrão         | `#e2e8f0`                         |
| Borda Quente         | `#eceae4`                         |
| Borda Interativa     | `rgba(28,28,28,0.4)`              |
| Botão Dark BG        | `#1c1c1c`                         |
| Botão Dark Texto     | `#fcfbf8`                         |
| Sombra Foco          | `0 0 0 3px rgba(47,128,237,0.15)` |
| Sombra Foco Warm     | `rgba(0,0,0,0.1) 0px 4px 12px`    |

### Comandos Rápidos de Componente

* **"Criar header"**: `70px` altura, fundo `#fff`, borda inferior `3px solid #337259`, sombra `0 1px 3px rgba(0,0,0,0.05)`.
* **"Criar sidebar"**: `250px` largura, fundo `#fff`, scroll próprio, links com padding `0.75rem 1.25rem`.
* **"Criar card de métrica"**: Fundo `#fff`, borda `1px solid #e2e8f0`, raio `6px`, padding `1.25rem`, ícone com fundo pastel + cor temática.
* **"Criar card de conteúdo"**: Fundo `#fff`, borda `1px solid #e2e8f0`, raio `12px`, padding `1.5rem`.
* **"Criar botão primário"**: Fundo `#2f80ed`, texto `#fff`, raio `6px`, padding `0.65rem 1.25rem`.
* **"Criar botão dark"**: Fundo `#1c1c1c`, texto `#fcfbf8`, padding `8px 16px`, raio `6px`, inset shadow triplo.
* **"Criar botão ghost"**: Transparente, borda `1px solid rgba(28,28,28,0.4)`, raio `6px`, padding `8px 16px`.
* **"Criar stats bar"**: Layout horizontal flex, valores `36px`–`48px` peso `600` letter-spacing `-1.2px`, rótulos `16px` peso `400` cor `#64748b`.
* **"Criar input"**: Fundo `#f7f4ed`, borda `1px solid #e2e8f0`, raio `6px`, placeholder `#64748b`.
* **"Criar página"**: Shell com header + sidebar + `.app-content` (flex:1, overflow-y:auto, padding `1.5rem 2rem`).

### Regras de Ouro

1. Sempre usar Montserrat como fonte.
2. Sempre usar PrimeFlex para layout, grid, flexbox e espaçamento.
3. Fundo da página = `#f7f4ed`, nunca branco puro.
4. Cards usam bordas para contenção, não sombras pesadas.
5. Peso 700 só em H1, títulos de cards e KPIs.
6. Letter-spacing negativo em headlines ≥ 36px.
7. Botões dark usam inset shadow triplo.
8. Border-radius segue a escala: `4 → 6 → 8 → 12 → 16 → 20 → 50% → 9999px`.
9. Header = `70px` com friso verde `#337259`. Nunca alterar.
10. Sidebar = `250px` com scroll próprio.

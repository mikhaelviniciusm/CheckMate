# CheckMate

Este é um aplicativo simples para gerenciamento de tarefas, onde você pode adicionar, marcar como concluídas e gerenciar o status das suas tarefas. O app oferece a opção de alternar entre modos claro e escuro, proporcionando uma experiência de uso confortável e personalizada. Além disso, o app conta com uma interface simples e intuitiva, permitindo ao usuário gerenciar suas atividades diárias de maneira eficiente.

## Funcionalidades

✔️ Adicionar tarefas  
✔️ Marcar tarefas como concluídas  
✔️ Editar ou excluir tarefas existentes  
✔️ Alternar entre modo claro e modo escuro  
✔️ Armazenamento de tarefas em banco de dados local  
✔️ Alterar o idioma do aplicativo (Atualmente apenas Português e Inglês)  
✔️ Excluir todas as tarefas com um clique  

## Tecnologias Utilizadas

- 💻 **Android Studio**: Ambiente de desenvolvimento integrado (IDE) para criação de aplicativos Android.  
- 💻 **SQLite**: Banco de dados local para armazenamento das tarefas.  
- 💻 **Java**: Linguagem principal utilizada para a lógica do aplicativo.  

## Arquitetura

O aplicativo utiliza o padrão de arquitetura **MVC (Model-View-Controller)**, o que facilita a manutenção e a expansão do sistema.  

## Estrutura do Projeto

- **MainActivity**: Tela principal do aplicativo, onde o usuário pode visualizar e gerenciar suas tarefas.  
- **SettingsActivity**: Tela de configurações, onde o usuário pode alterar o idioma, forçar o modo escuro e limpar todas as tarefas.  
- **DatabaseHelper**: Classe responsável por gerenciar o banco de dados SQLite.  
- **Recursos de UI**: Interface simples e intuitiva, com suporte a animações e transições suaves.  

## Requisitos

- **Android 5.0 (Lollipop)** ou superior.  
- **Espaço de Armazenamento**: Aproximadamente 10 MB.  

## Instalação

1. Acesse a página de releases do repositório no GitHub:
   [CheckMate Releases](https://github.com/mikhaelviniciusm/CheckMate/releases)
2. Baixe o arquivo APK da última release disponível.
3. Transfira o APK para o seu dispositivo Android.
4. No dispositivo, habilite a instalação de aplicativos de fontes desconhecidas (se necessário).
5. Instale o APK e comece a usar o aplicativo.

## Como Usar

- **Adicionar Tarefas**: Insira o nome da tarefa na barra inferior e clique no botão de adicionar.  
- **Marcar como Concluída**: Toque na tarefa para marcá-la como concluída ou desmarcá-la.  
- **Editar ou Excluir Tarefas**: Segure em cima de uma tarefa para editar ou excluí-la.  
- **Forçar tema escuro**: Vá para as configurações e ative a chave da função.  
- **Alterar Idioma**: CLique na opção idioma presente nas configurações e escolha entre Português e Inglês.  
- **Excluir Todas as Tarefas**: Use a opção de limpar todas as tarefas para começar do zero.  

## Contribuidores

Este aplicativo foi desenvolvido com a colaboração de:

- [Mikhael Vinícius](https://github.com/mikhaelviniciusm)
- [Estevão Oliveira](https://github.com/Estevao750)
- [Lívia Sydrião](https://github.com/liviasydriao)

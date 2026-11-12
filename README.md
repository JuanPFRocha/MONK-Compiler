# Compilador da Linguagem MONK

Este projeto contém o compilador da linguagem MONK, 
desenvolvido em Java.

## Requisitos

- Java (JDK ou JRE) instalado e configurado no `PATH`.
- O arquivo `lexico-1.0-SNAPSHOT.jar` gerado na pasta `target`.
- Um arquivo de código MONK para ser analisado (por exemplo, `example.txt`).

Para verificar se o Java está instalado, abra o CMD e execute:

```
java -version
```

## Como executar

### 1. Abrir o CMD

1. Pressione a tecla `Windows`.
2. Digite `cmd`.
3. Clique em **Prompt de Comando**.

### 2. Entrar na pasta do projeto


```
cd C:\caminho\para\o\projeto\lexico
```

> Substitua o caminho acima pelo local onde o projeto está salvo no seu computador.

### 3. Executar o analisador

Com o CMD aberto na pasta do projeto, execute:

```
java -jar .\target\lexico-1.0-SNAPSHOT.jar
```

### 4. Informar o nome do arquivo

Após iniciar, o analisador irá pedir o nome do arquivo que está o código-fonte em MONK, após digitar, pressione `Enter`:

```
example.txt
```

O analisador irá ler o arquivo e exibir os tokens encontrados.


## Observações

- O arquivo `example.txt` deve estar na pasta em que o comando foi executado (ou informe o caminho completo até ele).
- Para analisar outro programa, basta informar o nome de outro arquivo no passo 4.
- Caso apareça o erro `'java' não é reconhecido como um comando interno ou externo`, o Java não está instalado ou não foi adicionado ao `PATH`.
- Caso apareça o erro `Unable to access jarfile`, confirme que o projeto foi compilado e que o arquivo `lexico-1.0-SNAPSHOT.jar` existe dentro da pasta `target`.
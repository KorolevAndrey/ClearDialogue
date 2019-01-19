# JDialogue
JDialogue is a branching dialogue editor for video games. Designed to be extremely flexible and work with any type of game engine.

![jdialogue](https://user-images.githubusercontent.com/6147299/51421782-4c789e00-1b69-11e9-8cd6-98c1a887f946.png)

## Features

#### General 
- Works with numerous languages
- Supports both normal dialogue and dialogue responses
- Use the node-based system to connect these and make complex branching dialogue paths
- Has a game API that can be referenced directly from your Java game for even further simplicity of use
- Lightweight and simple to use

#### IO
- Supports JSON exporting/importing
- Has documented tools for how to add support for your own filetypes

#### Tools
- Has replace/find tools that support refactoring within the current project as well as multiple projects spanning multiple files
- Supports implementing custom syntax highlighting (I.E. highlighting text commands from your particular engine)

#### Documentation
- Includes test applications in the source code showing how to implement JDialogue into your games
- Code-base is highly customizable and easy to modify
- Code-base has extremely detailed documentation that walks you through adding new functionality

## Dependencies
For simply running JDialogue as a jar, all you need is to have [Java installed](https://java.com/en/).

For development, JDialogue uses JDK8 and its version of JavaFX. All other used APIs are included with JDialogue via Gradle, and are [credited in the license.](https://github.com/SkyAphid/JDialogue/blob/master/LICENSE)

## Implementing JDialogue in Your Game
- For examples of importing/exporting JDialogue files, check [`nokori.jdialogue.io`](https://github.com/SkyAphid/JDialogue/tree/master/JDialogue/src/nokori/jdialogue/io)
- For examples of implementing JDialogue into your game, see [`nokori.jdialogue.test`](https://github.com/SkyAphid/JDialogue/tree/master/JDialogue/src/nokori/jdialogue/test)

## Download
[Get a runnable version of JDialogue on the releases page.](https://github.com/SkyAphid/JDialogue/releases)

<h1 align="center"> Prova Finale di Ingegneria del Software - AA 2023-2024 </h1>

![Loading.png](PROGETTO%2Fsrc%2Fmain%2Fresources%2Fimages%2Fothers%2FLoading.png)
## Authors (Group GC34).
- [Maria Concetta Santagata](https://github.com/mariaconcetta03)
- [Andrea Rubagotti](https://github.com/Ruba750)
- [Francesco Raimondi](https://github.com/FraRai02)
- [Chiara Polizzi](https://github.com/chiararaihc)
## Implementation of Codex Naturalis, a board game.
### Project specification
Codex Naturalis is the final test of "Software Engineering", a course of "Bachelor Degree in Computer Science Engineering" held at Politecnico di Milano (Academic Year 2023/24).
### Implemented Functionalities
|       Functionalities        | Implemented        |
|:----------------------------:| :-------------:    |
|         Basic Rules          | ✅ |
|        Complete Rules        | ✅ |
|             TCP              | ✅ |
|             RMI              | ✅ |
|             TUI              | ✅ |
|             GUI              | ✅ |
|       Multiple Matches       | ✅ |
|             Chat             | ❌ |
|         Persistence          | ❌ |
| Resilience to disconnections | ❌    |
###### Legend: ❌ Not Implemented     ✅ Implemented
## Documentation
### Test Coverage
You can check up our JUnit test coverage [here](Deliverables%2FTEST%20COVERAGE%2FtestCoverage.png).
### UML
Here you can check our UML diagrams:
- [First Peer Review UML](Deliverables%2FFILES%20CONSEGNATI%2026-03%2FUML_v3.jpg)
- [Second Peer Review UML](Deliverables%2FFILES%20CONSEGNATI%2006-05%2022_45)
- [High level UML Server-Client](Deliverables%2FUMLs%2FUML%20ALTO%20LIVELLO.png)
- [Detailed UML Server-Client](Deliverables%2FUMLs%2FUML%20DETTAGLIO.png)
- [Communication Protocol Diagrams](Deliverables%2FNETWORK%20SEQUENCE%20DIAGRAMS)
### Jar
You can download the Jar to launch the game [here](Deliverables%2FJAR).
## How to run
### Server
1. Open the prompt as **administrator**.
2. Deactivate your antivirus and firewall services 
[```BE CAREFUL! Do that only in a safe network```]
```bash
netsh advfirewall set allprofiles state off
```
3. Allow the prompt to show more characters in a single row: ```Prompt``` ➡ ```(Right click) Properties``` ➡ ```Layout``` ➡ ```Deselect "Text output wraps when resizing"``` ➡ ```Setting "9000" as width``` ➡ ```OK```
4. Launch jar file using the following command (**pay attention to your directory**): 
```bash
java -jar pathToServerLauncher\ServerLauncher.jar
```
5. Insert Server Ip address or press enter for localhost.
6. When finished playing, remember to reactivate your security settings:
```bash
netsh advfirewall set allprofiles state on
```
### Client
1. Open the prompt as **administrator**.
2. Deactivate your antivirus and firewall services
      [```BE CAREFUL! Do that only in a safe network```]
```bash
netsh advfirewall set allprofiles state off
```
3. Set the registry of Windows to let it recognise colors using the following command:
```bash
reg add HKEY_CURRENT_USER\Console /v VirtualTerminalLevel /t REG_DWORD /d 1
```
4. Allow the prompt to show more characters in a single row: ```Prompt``` ➡ ```(Right click) Properties``` ➡ ```Layout``` ➡ ```Deselect "Text output wraps when resizing"``` ➡ ```Setting "9000" as width``` ➡ ```OK```
5. Launch jar file using the following command (**pay attention to your directory**):
```bash
java -jar pathToClientLauncher\ClientLauncher.jar
```
6. Insert Server Ip address or press enter for localhost.
7. Select the communication protocol and the UI preferred.
8. _(Only for RMI)_ Insert Client Ip address or press enter for localhost.
9. When finished playing, remember to reactivate your security settings:
```bash
netsh advfirewall set allprofiles state on
```

## How to play
1. When you launch the clientJar you will be able to choose your preferred network protocol (RMI/TCP) and user Interface (TUI/GUI).
2. You will then proceed with the nickname selection.
3. After that you will be asked if you want to create a new lobby or join an already existing one.
4. The game will start when the correct number of connected clients is reached.




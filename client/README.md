# Mars web project group-14

# ***Marsonality - Client*** 🪐
### **Description**
A tool created to unlock human traits and personalize their own profiles with accrued traits for fun and profit.

---

## **Table of Contents** 📓
1. Important public urls
2. Installation
3. Bug list
---

## **1. Important public urls** 🧷 
* Web project: https://project-ii.ti.howest.be/mars-14/
* Sonar reports: https://sonar.ti.howest.be/dashboard?id=2022.project-ii%3Amars-client-14
* Wireframes: [Link to figma](https://www.figma.com/proto/ZhAeyCkFOz2ucG7iRTvfVV/wire-fames?page-id=0%3A1&node-id=6%3A3&viewport=668%2C-117%2C0.11&scaling=scale-down&starting-point-node-id=6%3A3&show-proto-sidebar=1)

---

## **2. Installation instructions for further development of the project** 🧱 

Link to client repository [here](https://git.ti.howest.be/TI/2022-2023/s3/analysis-and-development-project/projects/group-14/client)

Clone the **client** repo with SSH:
```git@git.ti.howest.be:TI/2022-2023/s3/analysis-and-development-project/projects/group-14/client.git```

### Install **npm** with following commands:

- Open a terminal in your IDE
- Make sure you are in the root folder of the client project.
> npm install

Execute for **linux/mac** users.

> npm run validate-local

for **Windows** users. 

> npm run validate-local-win

- If there are errors, the program execution will halt and show the first error
- If there are no errors, a report file will be generated in the `.scannerworks/` directory. 
    - You will find the link to the sonar report in this file


### Install **sass**
> npm install -g sass

### Install **SonarLint**
- In IntelliJ: 
    -  Go to File >> Plugins >> Type ‘SonarLint’ >> Install and Restart IDE.
- Add the sonarQube connection binding
    - File >> Setting >> Tools >> SonarLint >> Click on + sign on right side
- Further instructions for authentication tokens: https://medium.com/@tarunchhabra/using-sonarlint-with-sonarqube-in-intellij-ide-5128111d1b8d


## Instructions for testing locally
* Run the mars-server with gradle run (through your IDE)
* Open the mars-client in phpstorm/webstorm
  * Navigate to the index.html
  * Click on a browser icon at the top right of your IDE to host the mars-client.
* Make sure to execute the following commands in **browser console** *(= You get to see a map telling you're not chipped yet.)*:
  * `setOpenApiToken()` 
    * This makes sure you can use the endpoints with a security authorisation token.
    * This allows the PushAPI to send you notifications *(you may also need to accept them manually on your browser and device)*
  * `setCurrentMartian(1)` (where "1" is the marsId of a Martian) => This makes sure **you** are using the application as the martian (with the specified marsId).
* **Don't see any data/many undefined data?**
  * Your localstorage may be outdated due to self-executed manipulation to the martian.
    * Repeat the step above to solve.
  * Your **cache** needs to be cleared
  
## Instruction for testing the web client locally with a deployed mars-server
* Open the mars-client in phpstorm
  * Copy the following settings to **config.json**
```json
      {
        "host": "https://project-ii.ti.howest.be",
        "folder": "",
        "group": "mars-14"
      }
```
  * Navigate to the index.html
  * Click on a browser icon at the top right of your IDE to host the mars-client.
  * Make sure to undo the settings once you are done testing the remote server!
  
---

  ## **3. Bug list** 🦟

| Bug          | Fixed?     |
|--------------|-----------|
| number of profile was always -1 in GET /api/martians/{marsId}/profiles/{profileName} endpoint | Yes      |
| The drag and drop only 'reacts' to its functionality when you 'drop' it on the container (not on but next to the list-items (=traits))     | No  |
| The map icons on the index page show up to early  
![](https://i.gyazo.com/3a35ded5ba8a32c7f272e237e50f8b6e.png) | No


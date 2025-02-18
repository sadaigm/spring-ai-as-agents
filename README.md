# Spring AI As Agents

This is a Spring Boot application designed to leverage AI as an agent. The application aims to empower existing Spring services by integrating AI capabilities, thereby enhancing results and improving the overall customer experience. The project uses Spring AI with Ollama to provide advanced AI capabilities and seamless interactions.

## Prerequisites

Before you begin, ensure you have met the following requirements:
- You have installed Java 17 or later.
- You have installed Maven.
- You have a basic understanding of Spring Boot.

## Installation

To install and run the Spring AI As Agents Application, follow these steps:

1. Clone the repository from your GitHub repository.

    ```     
   git clone https://github.com/sadaigm/spring-ai-as-agents.git
2. Navigate to the project directory. 
    ```
   cd spring-ai-as-agents
3. Build the project using Maven with the command.
    ```
   mvn clean install
4. Run the application using the following command.
   ```
   mvn spring-boot:run
5. Test the application api using 
    ```
   curl --location --request POST 'http://localhost:8080/create-team?userInput=write%20a%20story%20for%208%20year%20boy%20highlighting%20honesty?&teamName=Story%20Teller' \
   --header 'Content-Type: application/json' \
   --data-raw '{
   "model": "qwen2.5-coder:0.5b",
   "prompt": "The sky is blue because of Rayleigh scattering",
   "stream": false
   }'

## Features

The Spring AI As Agents application includes the following features:

- **Team Creation:** Allows users to create new team instances by providing a team name and user input.
- **Team Invocation:** Enables users to start and invoke existing team instances using team ID, team name, and user input.
- **Retrieve Team Instance:** Provides functionality to get the details of a specific team instance using its team ID.
- **Error Handling:** Ensures that required parameters such as user input and team name are provided, with error handling for missing or blank values.
- **Default Team Implementation:** The current implementation includes a default team with two predefined roles: a researcher and a reviewer.
- **In-Memory Storage:** Currently, all storage is in-memory for simplicity and ease of development. The application manages the in-memory storage of team contexts, team instances, and teams.
- **Spring AI Starter with Ollama:** Integrates Spring AI with Ollama to provide advanced AI capabilities and seamless interactions. The project uses `spring-ai-ollama-spring-boot-starter 1.0.0-M5`.

## Usage

Once the application is up and running, you can access it at `http://localhost:8080`. The application can be extended by adding more classes and components as needed to enhance its functionality.

## Default Team Implementation

The default team implementation includes two predefined agents:

- **Researcher:**
    - Role: Research Assistant
    - Responsibilities: Perform web searches, analyze information, break down complex queries, verify information, and provide research summaries.

- **Reviewer:**
    - Role: Research Verification Specialist
    - Responsibilities: Verify the effectiveness of content, suggest improvements, explore additional angles or perspectives, track progress, and provide detailed summaries.

## Conversation Management

The application maintains a list of conversation messages, allowing seamless interactions between different agents and the user. Each message contains the content, role, and timestamp, ensuring a smooth workflow and coordination among team members.

## Future Enhancements

Plans are in place to enhance the application by implementing a real-time chat service experience using Redis. This will provide improved performance and scalability for storing and retrieving team contexts, team instances, and teams.

## Contributing

Contributions are always welcome! Please follow these steps to contribute:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes and commit them (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Create a Pull Request.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

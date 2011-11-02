#BRIL
Biophysical repositories in the lab

## What is this?

The Randall Division of Cell and Molecular Biophysics is a strongly interdisciplinary research division in the School of Biomedical & Health Sciences at King's College London. The Division includes a variety of research groups addressing different topics within this field. This project (BRIL) aims to enhance the repository facilities at the Randall Division by:

* Embedding the repository within the researchers' day-to-day research and experimental practices.
* Allowing data and metadata to be captured in automated fashion, for example from equipment or processing and analysis software
* Allowing the structure of experimental processes as a whole to be captured, modelled and stored within the repository, rather than just the individual data sets
* Enhancing browse and access facilities so that users can explore and re-use these complex representations, and data exchange facilities to increase interoperability with other repositories in biomedical disciplines.
* Integrating the repository into the wider King's infrastructure, and in particular the Institutional preservation practices and policies

## Components

### DirectoryWatcher
<<<<<<< HEAD
This is a Java application that runs on the biophysicist's machine in the lab. When the application detect new file(s) are added to a specified folder, it sends a message using the JMS API to the message queue provided by [ActiveMQ](http://activemq.apache.org/) (a message broker) running on the server. It also uploads the file to a temporary location on the server for ingesting into the [Fedora Commons](http://fedora-commons.org/) repository.

### BRIL2
This is a Java application running on the server in conjunction with ActiveMQ. When a message is received on the queue, the application characterizes the incoming file and creates a Submission Information Packages (SIP) for ingest into the Fedora Commons data repository. It then ingests the file into Fedora and generate relationships between the file and other files in the repository based on biophysical experimental workflows. This essentially allows the capturing of the entire experiment process.

### mybril
This is a Django + JavaScript + HTML5 based web application for browsing and exploring the experiment process. When an user logs into the system, he/she can interactively visualize the relationships captured by the BRIL2 application. The application first uses the [eulfedora Python module](https://github.com/emory-libraries/eulfedora) to extract RDF from Fedora Commons, then the RDF is converted into a JSON for display in a forced directed graph based visualization. It also uses the [foresite toolkit](http://code.google.com/p/foresite-toolkit/) to generate [OAI-ORE (Open Archives Initiative Object Reuse and Exchange)](http://www.openarchives.org/ore/) resource maps that can be used for data exchange between repositories.
=======
This is a Java application that runs on the biophysicist's machine in the lab. When the application detect new file(s) are added to a specified folder, it sends a message using the JMS API to the message queue provided by [ActiveMQ](http://activemq.apache.org/) (a message broker) running on the server. It also uploads the file to a temporary location on the server for ingesting into the Fedora Commons repository.

### BRIL2
This is a Java application running on the server in conjunction with ActiveMQ. When a message is received on the queue, the application characterizes the incoming file and creates a Submission Information Packages (SIP) for ingest into the Fedora Commons data repository. It then ingests the file into Fedora and generate relationships between the file and other files in the repository based on biophysical experimental workflows. This allows the capturing of the entire experiment process.

### mybril
This is a Django + JavaScript + HTML5 based web application for browsing and exploring the experiment process. When an user logs into the system, he/she can interactively visualize the relationships captured by the BRIL2 application. The application first uses the [eulfedora Python module](https://github.com/emory-libraries/eulfedora) to extract RDF from Fedora Commons, then the RDF is converted into a JSON for display in a Forced Directed Graph visualization. It also uses the [foresite toolkit](http://code.google.com/p/foresite-toolkit/) to generate OAI-ORE resource maps that can be used for data exchange between repositories.
>>>>>>> Added README.md

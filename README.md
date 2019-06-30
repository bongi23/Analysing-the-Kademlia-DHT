# Analysing-the-Kademlia-DHT
First mid term for the *P2P and Blockchain* course of the University of Pisa, M. Sc. in Computer Science, ICT Solutions Architect curriculum.

# Brief description
This homework aims at analysing the behaviour of a Kademlia network, from a *graph analysis point-of-view*, when changing the various parameter of the protocol. The requirements of this assignment can be found into *Assignment.pdf* while into *Analysing_the_Kademlia_DHT_report.pdf* there is a deep description of the code, the main projectual choices and the graph analysis of the obtained networks. 

# Usage

Main method is in Coordinator.java.

Compile with `javac Coordinaor.java` 

Execute with `java Coordinator size_of_network size_of_ids size_of_buckets`

Networks are saved in a file called `n_m_k.csv`: n, m and k are the three input parameters of main method.

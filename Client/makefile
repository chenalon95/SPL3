CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_thread -lboost_system -pthread

all: Client
	g++ -o bin/BGSclient bin/ConnectionHandler.o bin/echoClient.o bin/EncDec.o bin/UserWrite.o bin/UserRead.o $(LDFLAGS)
Client: bin/ConnectionHandler.o bin/echoClient.o bin/EncDec.o bin/UserRead.o bin/UserWrite.o

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/EncDec.o: src/EncDec.cpp
	g++ $(CFLAGS) -o bin/EncDec.o src/EncDec.cpp


bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp


bin/UserRead.o: src/UserRead.cpp
	g++ $(CFLAGS) -o bin/UserRead.o src/UserRead.cpp


bin/UserWrite.o: src/UserWrite.cpp
	g++ $(CFLAGS) -o bin/UserWrite.o src/UserWrite.cpp


.PHONY: clean

clean:
	rm -f bin/*

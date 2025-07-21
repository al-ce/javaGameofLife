set quiet := true

comp:
    javac -d bin -cp bin src/Life.java src/*/*.java

run:
    java -cp bin Life

crun:
    just comp && just run

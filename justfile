set quiet := true

comp:
    javac -d bin -cp bin src/Life.java src/*/*.java

run height="":
    java -cp bin Life {{ height }}

crun height="":
    just comp && just run {{ height }}

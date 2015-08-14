Tamir Arieli - 201548039
Shahar Levy - 203546379

The Main class is 'Main'
The assumptions we made:
    1. The points you enter while you play the game are like in the xml
       (the top point is (1,1))
    2. In the Load Game option users will need to insert full path to their saved game.

*Computer has good AI will always try to move to best point(regarding his target)


**It doesn't always happens but,
  We have noticed that when we transfer the source code between computers,
  We need to edit the file project.properties path:
  NetBeansProjects\Chinese-Checkers\nbproject\project.properties

  We need to change endorsed.classpath to this:
endorsed.classpath=\
    ${lib.JAX-WS 2.2.classpath}

  and then Regenerate JAXB Bindings.

We tried to fix this issue so it should work.
Please tell us if you encountered a problem.
shahar201091@gmail.com
tarieli@gmail.com


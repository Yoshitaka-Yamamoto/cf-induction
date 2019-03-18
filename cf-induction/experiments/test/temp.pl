%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% class/2 learns the class (mammal/fish/reptile/bird) of various animals.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Mode declarations

modeh(1,class(+animal, #class))?
modeb(1,has_milk(+animal))?
modeb(1,has_gills(+animal))?
modeb(1,has_covering(+animal,#covering))?
modeb(1,has_legs(+animal,#natt))?
modeb(1,homeothermic(+animal))?
modeb(1,has_eggs(+animal))?
modeb(1,habitat(+animal,#habitat))?




%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Negative examples treated as the background theory

% :- class(X,mammal), class(X,fish).
% :- class(X,mammal), class(X,reptile).
% :- class(X,mammal), class(X,bird).
% :- class(X,fish), class(X,reptile).
% :- class(X,fish), class(X,bird).
% :- class(X,reptile), class(X,bird).
% :- class(eagle,reptile).

:-class(trout,mammal).
:-class(herring,mammal).
:-class(shark,mammal).
:-class(lizard,mammal).
:-class(crocodile,mammal).
:-class(t_rex,mammal).
:-class(turtle,mammal).
:-class(eagle,mammal).
:-class(ostrich,mammal).
:-class(penguin,mammal).


has_milk(dog).
has_milk(dolphin).
has_milk(bat).
has_milk(platypus).


:-class(dog,fish).
:-class(dolphin,fish).
:-class(platypus,fish).
:-class(bat,fish).
:-class(lizard,fish).
:-class(crocodile,fish).
:-class(t_rex,fish).
:-class(turtle,fish).
:-class(eagle,fish).
:-class(ostrich,fish).
:-class(penguin,fish).


has_gills(trout).
has_gills(herring).
has_gills(shark).
has_gills(eel).



:-class(dog,reptile).
:-class(dolphin,reptile).
:-class(platypus,reptile).
:-class(bat,reptile).
:-class(trout,reptile).
:-class(herring,reptile).
:-class(shark,reptile).
:-class(eagle,reptile).
:-class(ostrich,reptile).
:-class(penguin,reptile).

has_covering(trout,scales).
has_covering(herring,scales).
has_covering(lizard,scales).
has_covering(crocodile,scales).
has_covering(t_rex,scales).
has_covering(snake,scales).
has_covering(turtle,scales).

:-class(dog,bird).
:-class(dolphin,bird).
:-class(platypus,bird).
:-class(bat,bird).
:-class(trout,bird).
:-class(herring,bird).
:-class(shark,bird).
:-class(lizard,bird).
:-class(crocodile,bird).
:-class(t_rex,bird).
:-class(turtle,bird).

has_covering(eagle,feathers).
has_covering(ostrich,feathers).
has_covering(penguin,feathers).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Background knowledge

has_covering(dog,hair).
has_covering(dolphin,none).
has_covering(platypus,hair).
has_covering(bat,hair).
has_covering(shark,none).
has_covering(eel,none).


has_legs(dog,four).
has_legs(dolphin,zero).
has_legs(platypus,two).
has_legs(bat,two).
has_legs(trout,two).
has_legs(herring,two).
has_legs(shark,zero).
has_legs(eel,zero).
has_legs(lizard,four).
has_legs(crocodile,four).
has_legs(t_rex,four).
has_legs(snake,zero).
has_legs(turtle,four).
has_legs(eagle,two).
has_legs(ostrich,two).
has_legs(penguin,two).




homeothermic(dog).
homeothermic(dolphin).
homeothermic(platypus).
homeothermic(bat).
homeothermic(eagle).
homeothermic(ostrich).
homeothermic(penguin).


habitat(dog,land).
habitat(dolphin,water).
habitat(platypus,water).
habitat(bat,air).
habitat(bat,caves).
habitat(trout,water).
habitat(herring,water).
habitat(shark,water).
habitat(eel,water).
habitat(lizard,land).
habitat(crocodile,water).
habitat(crocodile,land).
habitat(t_rex,land).
habitat(snake,land).
habitat(turtle,water).
habitat(eagle,air).
habitat(eagle,land).
habitat(ostrich,land).
habitat(penguin,water).

has_eggs(platypus).
has_eggs(trout).
has_eggs(herring).
has_eggs(shark).
has_eggs(eel).
has_eggs(lizard).
has_eggs(crocodile).
has_eggs(t_rex).
has_eggs(snake).
has_eggs(turtle).
has_eggs(eagle).
has_eggs(ostrich).
has_eggs(penguin).


has_milk(cat).
homeothermic(cat).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Types

animal(dog).
animal(dolphin).
animal(platypus).
animal(bat).
animal(trout). 
animal(herring). 
animal(shark). 
animal(eel).
animal(lizard). 
animal(crocodile). 
animal(t_rex). 
animal(turtle).
animal(snake). 
animal(eagle). 
animal(ostrich). 
animal(penguin).
animal(cat). 
animal(dragon). 
animal(girl). 
animal(boy).
class(mammal). 
class(fish). 
class(reptile). 
class(bird).
covering(hair). 
covering(none). 
covering(scales). 
covering(feathers).
habitat(land). 
habitat(water). 
habitat(air). 
habitat(caves).
natt(zero). 
natt(two). 
natt(four).




% Hypothesis obtained by CF-induction

% [class(X_0, reptile), -has_covering(X_0, scales), -habitat(X_0, land
% [class(X_0, mammal), -has_milk(X_0
% [class(X_0, reptile), -has_legs(X_0, 4), -has_eggs(X_0
% [class(X_0, bird), -has_covering(X_0, feathers
% [class(X_0, fish), -has_gills(X_0



class(eel, fish).
class(trout, fish).
class(dog, mammal).
generalise(class/2)?
test(test)?

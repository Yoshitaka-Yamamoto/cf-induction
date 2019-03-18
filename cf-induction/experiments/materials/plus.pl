%%% Mode declaration %%%

modeh(1, plus(+num, +num, -num))?
modeb(1, plus(-num, -num, -num))?
modeb(3, suc(-num, -num))?

% Target hypothesis

% plus(X, Y, Z) :- plus(X, V, U), suc(V, Y), suc(U, Z)

% The above corresponds to the equation X + Y = Z  if  X  + (Y - 1)  =  Z - 1.
% Note that the predicate suc(V, Y) means Y = V + 1.

% Note that the bridge theory F is set as B and -E in the experiment.

%%% Type %%%

num(0).
num(1).
num(2).
num(3).
num(4).
num(5).

%%% 10 Positive examples %%%

plus(1, 1, 2).
plus(1, 2, 3).
plus(2, 1, 3).
plus(1, 3, 4).
plus(3, 1, 4).
plus(1, 4, 5).
plus(4, 1, 5).
plus(2, 2, 4).
plus(2, 3, 5).
plus(3, 2, 5).

              
%:-plus(1,2,2).
%:-plus(2,3,4).

%%% Background theory %%%

suc(0, 1).
suc(1, 2).
suc(2, 3).
suc(3, 4).
suc(4, 5).

plus(X, 0, X).
plus(0, X, X).

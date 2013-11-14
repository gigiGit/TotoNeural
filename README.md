14-Nov-2013
Introdotto gestione delle partite.
Apprendimento della rete basato su due algoritmi:
  1) BackPropagation (classico) 
  2) Ricerca del minimo parziale su intervallo (reticolo) 
Suddivisa la rete in due possibili modalità 
  1) InputLayer (dim = nsquadre*2) [+ hiddenLayer1 (x)] [+ hiddenLayer2 (y)] + outputLayer (3 = 1X2)
  2) 3 reti ognuna specializzata sul risultato 1 = Vittoria in casa 2 = vittoria fuori casa X = pareggio
    InputLayer (dim = nsquadre*2) [+ hiddenLayer (x)] [+ hiddenLayer2 (y)] + outputLayer (1) 
    ....

  

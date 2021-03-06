from datetime import datetime, date
from collections import defaultdict

loc_offers = "data/offers.csv"
loc_transactions = "data/transactions.csv"

# will be created
loc_reduced = "data/reduced.csv" 
###

def reduce_data(loc_offers, loc_transactions, loc_reduced):
  start = datetime.now()
  #get all categories and comps on offer in a dict
  offers_cat = {}
  offers_co = {}
  for e, line in enumerate( open(loc_offers) ):
    offers_cat[ line.split(",")[1] ] = 1
    offers_co[ line.split(",")[3] ] = 1
  #open output file
  with open(loc_reduced, "wb") as outfile:
    #go through transactions file and reduce
    reduced = 0
    for e, line in enumerate( open(loc_transactions) ):
      if e == 0:
        outfile.write( line ) #print header
      else:
        #only write when if category in offers dict
        if line.split(",")[3] in offers_cat or line.split(",")[4] in offers_co:
          outfile.write( line )
          reduced += 1
      #progress
      if e % 5000000 == 0:
        print e, reduced, datetime.now() - start
  print e, reduced, datetime.now() - start

#reduce_data(loc_offers, loc_transactions, loc_reduced)

if __name__ == '__main__':
	reduce_data(loc_offers, loc_transactions, loc_reduced)
#ifndef _Bitmap_
#define _Bitmap_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "arch_types.h"
typedef struct lowestPrioBitmap{
INT32 bitmap_vector;
UINT8 lowest_priority;
};
extern Array *LowestPrioBitmap_Set
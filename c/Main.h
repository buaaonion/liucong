#ifndef _Main_
#define _Main_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "Mem.h"
typedef struct T_ImgRegDesc{
string imgName;
UINT8 *imgStart;
};
typedef struct T_Segement{
UINT32 startAddr;
INT32 offset;
UINT32 id;
};
typedef struct Segement_Info{
INT32 segements;
Array Segement_Set;
};
extern Array *ImgRegDesc_Set;

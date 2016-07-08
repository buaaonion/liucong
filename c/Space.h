#ifndef _Space_
#define _Space_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "Mem.h"
#include "List.h"
#include "Pgtable.h"
typedef enum {
TC_SHARE_SPACE = 0x01000000,
TC_COPY_SPACE = 0x02000000,
TC_NEW_SPACE = 0x04000000
} spaceSetUpAttr;
typedef struct T_AddressSpace{
T_ID spid;
T_Link list;
T_Mutex lock;
T_MmContext *pgd;
T_CapList capList;
INT32 ktcbRefs;
};

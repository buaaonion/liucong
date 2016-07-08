#ifndef _Capability_
#define _Capability_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "List.h"
#include "error_code.h"
typedef struct T_Capability{
T_Link list;
T_ID capid;
T_ID owner;
T_ID resid;
UINT32 type;
UINT32 access;
ULONG start;
ULONG end;
ULONG size;
ULONG used;
UINT32 attr;
T_ID irq;
};
extern errorCode *capCheck(List capSet, capType cType, capAccess cAccess, errorCode *RETURN_CODE);

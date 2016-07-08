#ifndef _Ipc_
#define _Ipc_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "ThreadDecls.h"
#define IPC_EXTENDED_MAX_SIZE 256
#define MAX_BUFFER_SIZE 8192
typedef struct Ipc_Buffer{
UINT32 *addr;
INT8 buffer;
};
typedef struct Ipc_Tag{
UINT32 ipctag;
UINT32 *addr;
};
extern Array *Ipc_Buffer_Set;
extern Array *Ipc_Tag_Set;

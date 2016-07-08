#ifndef _Mem_
#define _Mem_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "arch_types.h"
typedef struct T_TaskContext{
UINT32 reg0;
UINT32 reg1;
UINT32 reg2;
UINT32 reg3;
UINT32 reg4;
UINT32 reg5;
UINT32 reg6;
UINT32 reg7;
UINT32 reg8;
UINT32 reg9;
UINT32 reg10;
UINT32 reg11;
UINT32 reg12;
UINT32 reg13;
UINT32 reg14;
UINT32 reg15;
UINT32 reg16;
UINT32 reg17;
UINT32 reg18;
UINT32 reg19;
UINT32 reg20;
UINT32 reg21;
UINT32 reg22;
UINT32 reg23;
UINT32 reg24;
UINT32 reg25;
UINT32 reg26;
UINT32 reg27;
UINT32 reg28;
UINT32 reg29;
UINT32 reg30;
UINT32 reg31;
UINT32 cr;
UINT32 pc;
UINT32 msr;
};
typedef struct T_ExregsContext{
UINT32 reg0;
UINT32 reg1;
UINT32 reg2;
UINT32 reg3;
UINT32 reg4;
UINT32 reg5;
UINT32 reg6;
UINT32 reg7;
UINT32 reg8;
UINT32 reg9;
UINT32 reg10;
UINT32 reg11;
UINT32 reg12;
UINT32 cr;
UINT32 pc;
UINT32 msr;
};
typedef enum {
MAP_FAUL,
MAP_USR_RW,
MAP_USR_RO,
MAP_KERN_RW,
MAP_USR_IO,
MAP_KERN_IO,
MAP_USR_RWX,
MAP_KERN_RWX,
MAP_USR_RX,
MAP_KERN_RX,
MAP_UNMAP
} pageAttr;
typedef struct T_MmContext{
void *page_dir_base;
UINT32 pid0;
INT32 modified;
};
typedef INT32 pExregs;
extern Array *PExregs_Set;

#ifndef _Processor_
#define _Processor_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "arch_types.h"
#define SCHED_PENDING 0x80000000
#define IRQ_IN_PROGRESS !(SCHED_PENDING)
typedef struct CPU_State{
UINT32 flags;
UINT32 irqNestingCounter;
};

#ifndef OOP_UTIL_LIST_H_INCLUDED_
#define OOP_UTIL_LIST_H_INCLUDED_
#include "BStar.h"

typedef void* List_ElementAddr;
typedef struct List_Node *List_PtrToNode;

struct List_Node {
	BStar_Object elemAddr;
	List_PtrToNode next;
};
typedef List_PtrToNode List_Position;

typedef struct List {
	int size;
	List_PtrToNode head;
	List_PtrToNode tail;
	BStar_Equal elemEqual;
} List;

/* Create a singly list with dummy head node */
extern List* List_new(BStar_Equal equal);

/* Dispose the list */
extern void List_destroy(List* list);

/* Insert an element with given position pos in the List list */
extern proposition List_insert(List* list, BStar_Object p, BStar_Object e);

/* Insert an element with given position pos in the List list */
//void List_quikInsert(Position pos, ElementAddr e);
/* Delete the first node in List list when x is found */
extern proposition List_delete(List* list, BStar_Object e);

extern void List_delete_List(List* list, List* list2);

extern proposition List_delete2(List* list, BStar_Object e, BStar_Equal equal);

/* Retrun true if List list is emtpy */
extern int List_isEmpty(List* list);

/* Make the given list empty */
extern void List_clear(List* list);

/* Return the postion of x in List list; NULL if not found */
extern BStar_Object List_get(List* list, BStar_Object e);

extern BStar_Object List_get_poi(List* list, int poi);

extern BStar_Object List_get2(List* list, BStar_Object e, BStar_Equal equal);

/* Return the postion of x in List list; NULL if not found */
extern BStar_Object List_prior(List* list, BStar_Object e);

/* Return the postion of x in List list; NULL if not found */
extern BStar_Object List_next(List* list, BStar_Object e);

/* Return the element number of List list */
extern int List_size(List* list);

extern void List_add(List* list, BStar_Object e);

extern void List_add_List(List* list, List* list2);

extern void List_add_list(List* list, List* list2);

#endif


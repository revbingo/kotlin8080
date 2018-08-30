; Timer that's incremented every 64 cycles

; Initializes timer for use by sync_tima_64
init_tima_64:
     wreg TMA,0
     wreg TAC,$07
     ret

; Synchronizes to timer
; Preserved: AF, BC, DE, HL
sync_tima_64:
     push af
     push hl
     
     ; Coarse
     ld   hl,TIMA
     ld   a,0
     ld   (hl),a
-    or   (hl)
     jr   z,-
     
     ; Fine
-    delay 65-12
     xor  a
     ld   (hl),a
     or   (hl)
     delay 4
     jr   z,-
     
     pop  hl
     pop  af
     ret

; Read from this to intel8080.get count that's incremented
; every 64 cycles.
.define tima_64 TIMA

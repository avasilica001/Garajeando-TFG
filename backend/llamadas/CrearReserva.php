<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdUsuario']) and isset($_POST['IdComunidad']) and isset($_POST['IdCoche']) and isset($_POST['FechaHoraInicio']) and isset($_POST['FechaHoraFin']) and isset($_POST['IdOferta'])){
                        $bbdd = new DBOperations();

                        if($bbdd->crearReserva($_POST['IdUsuario'],$_POST['IdComunidad'],$_POST['IdCoche'],$_POST['FechaHoraInicio'],$_POST['FechaHoraFin'],$_POST['IdOferta'])){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = "La reserva se ha creado correctamente.";
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No se ha podido crear la reserva.";
                        }
                }else{
                        $respuesta['error'] = true;
                        $respuesta['mensaje'] = "Faltan campos obligatorios.";
                }
        }else{
                $respuesta['error'] = true;
                $respuesta['mensaje'] = "Petición no válida.";
        }

echo json_encode($respuesta);
?>

<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdUsuario']) and isset($_POST['Matricula']) and isset($_POST['IdReserva']) and isset($_POST['IdUsuarioReserva']) and isset($_POST['Fecha']) and isset($_POST['Puntos']) and isset($_POST['Minutos'])){
                        $bbdd = new DBOperations();

                        if($bbdd->guardarPuntosResena($_POST['IdUsuario'],$_POST['Matricula'],$_POST['IdReserva'],$_POST['IdUsuarioReserva'],$_POST['Fecha'],$_POST['Puntos'],$_POST['Minutos'])){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = "Puntos guardados correctamente.";
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No se ha podido guardar los puntos.";
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


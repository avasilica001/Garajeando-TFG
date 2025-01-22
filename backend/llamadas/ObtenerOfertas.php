<?php
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='GET'){
                if (isset($_GET['IdUsuario']) and isset($_GET['IdComunidad']) and isset($_GET['FechaHoraInicio']) and isset($_GET['FechaHoraFin'])){
                        $bbdd = new DBOperations();

                        $resultado = $bbdd->obtenerOfertas($_GET['IdUsuario'],$_GET['IdComunidad'],$_GET['FechaHoraInicio'],$_GET['FechaHoraFin']);
                        if($resultado != 0){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = $resultado;
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No hay ofertas para mostrar.";
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

<?php
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='GET'){
                if (isset($_GET['IdUsuario']) and isset($_GET['IdComunidad'])){
                        $bbdd = new DBOperations();

                        $resultado = $bbdd->obtenerCoches($_GET['IdUsuario'],$_GET['IdComunidad']);
                        if($resultado != 0){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = $resultado;
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No hay comunidades para mostrar.";
                        }
                        $resultado2 = $bbdd->obtenerCochesOtrasComunidades($_GET['IdUsuario'],$_GET['IdComunidad']);
                        $respuesta['cochesOtrasComunidades'] = $resultado2;

                        $resultado3 = $bbdd->obtenerOfertasFuturas($_GET['IdUsuario'],$_GET['IdComunidad']);
                        $respuesta['ofertasFuturas'] = $resultado3;

			$resultado4 = $bbdd->obtenerOfertasPasadas($_GET['IdUsuario'],$_GET['IdComunidad']);
                        $respuesta['ofertasPasadas'] = $resultado4;

			$resultado5 = $bbdd->obtenerReservasPendientes($_GET['IdUsuario'],$_GET['IdComunidad']);
                        $respuesta['reservasPendientes'] = $resultado5;

			$resultado6 = $bbdd->obtenerReservasFuturas($_GET['IdUsuario'],$_GET['IdComunidad']);
                        $respuesta['reservasFuturas'] = $resultado6;

			$resultado7 = $bbdd->obtenerReservasPasadas($_GET['IdUsuario'],$_GET['IdComunidad']);
                        $respuesta['reservasPasadas'] = $resultado7;

			$resultado8 = $bbdd->obtenerReservasPorResenar($_GET['IdUsuario'],$_GET['IdComunidad']);
                        $respuesta['reservasResenar'] = $resultado8;
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
